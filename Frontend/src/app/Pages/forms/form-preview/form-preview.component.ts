import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormService } from '../../../Services/form.service';
import { QuestionService } from '../../../Services/question.service';
import { SectionService } from '../../../Services/section.service';
import { Forms } from '../../../Models/form.model';
import { QuestionResponse, QuestionType, SelectionMode } from '../../../Models/question.model';
import { SectionResponse } from '../../../Models/section.model';
import { SubmissionService } from "../../../Services/submission.service";
import { ResponsesService } from '../../../Services/responses.service';
import { firstValueFrom, forkJoin } from 'rxjs';



@Component({
  selector: 'app-form-preview',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './form-preview.component.html',
  styleUrl: './form-preview.component.css'
})
export class FormPreviewComponent implements OnInit {
  formId!: number;
  form: Forms | null = null;
  sections: SectionResponse[] = [];
  questions: QuestionResponse[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Para vista pública: guardar el token
  publicToken: string | null = null;

  trueFalseAnswer: Map<number, boolean> = new Map();

  // Respuestas del usuario (simuladas)
  userAnswers: Map<number, any> = new Map();

  // Estado de paginación por secciones
  currentSectionIndex = 0;
  questionsPerPage: QuestionResponse[] = [];

  // Preguntas agrupadas por sección
  questionsBySectionId: Map<number | null, QuestionResponse[]> = new Map();
  sectionOrder: (number | null)[] = [];

  // Enums para el template
  QuestionType = QuestionType;
  SelectionMode = SelectionMode;

  userId: number | null = localStorage.getItem('user_id') ? +localStorage.getItem('user_id')! : null;

  // Estado del formulario
  formStatus: 'not-started' | 'open' | 'closed' = 'open';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formService: FormService,
    private questionService: QuestionService,
    private sectionService: SectionService,
    private submissionService: SubmissionService,
    private responsesService: ResponsesService
  ) { }

  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token');
    if (token) {
      // Vista pública: cargar por token
      this.publicToken = token;
      this.loading = true;
      this.formService.getPublicForm(token).subscribe({
        next: (form) => {
          this.form = form;
          if (form.id) {
            this.formId = form.id;
          }
          this.checkFormStatus();

          // Verificar si el formulario requiere autenticación
          console.log({ form })
          if (!form.anonymousMode && !this.userId) {
            // Guardar la URL actual para regresar después del login
            const returnUrl = `/public/forms/${token}`;
            localStorage.setItem('returnUrl', returnUrl);
            alert('Este formulario requiere que inicies sesión.');
            this.router.navigate(['/login']);
            return;
          }

          this.loadSectionsAndQuestions();
          this.loading = false;
        },
        error: (err) => {
          console.error('Error al cargar formulario público:', err);
          this.errorMessage = 'Formulario público no encontrado o vencido';
          this.loading = false;
        }
      });
      return;
    }

    const id = this.route.snapshot.paramMap.get('formId');
    if (id) {
      this.formId = +id;
      this.loading = true;
      this.formService.getFormById(this.formId).subscribe({
        next: (form) => {
          this.form = form;
          this.checkFormStatus();
          // Usar el mismo método para cargar secciones y preguntas
          this.loadSectionsAndQuestions();
          this.loading = false;
        },
        error: (err) => {
          console.error('Error al cargar formulario:', err);
          this.errorMessage = 'Error al cargar el formulario';
          this.loading = false;
        }
      });
    } else {
      this.errorMessage = 'ID de formulario no encontrado';
    }
  }

  loadSectionsAndQuestions(): void {
    this.loading = true;

    // Cargar secciones
    this.sectionService.getSectionsByForm(this.formId).subscribe({
      next: (sections) => {
        this.sections = sections.sort((a, b) => a.position - b.position);

        // Cargar todas las preguntas
        this.questionService.getQuestionsByForm(this.formId, undefined).subscribe({
          next: (questionsWithoutSection) => {
            // Cargar preguntas de cada sección
            const sectionQueries = this.sections.map(section =>
              this.questionService.getQuestionsByForm(this.formId, section.id)
            );

            if (sectionQueries.length === 0) {
              // Solo hay preguntas sin sección
              this.questions = questionsWithoutSection.sort((a, b) => a.position - b.position);
              this.groupQuestionsBySections();
              this.loading = false;
              return;
            }

            // Usar forkJoin para cargar todas las secciones en paralelo
            forkJoin(sectionQueries).subscribe({
              next: (sectionQuestionsArrays) => {
                const allSectionQuestions = sectionQuestionsArrays.flat();
                this.questions = [...questionsWithoutSection, ...allSectionQuestions]
                  .sort((a, b) => a.position - b.position);
                this.groupQuestionsBySections();
                this.loading = false;
              },
              error: (err) => {
                console.error('Error al cargar preguntas de secciones:', err);
                this.questions = questionsWithoutSection.sort((a, b) => a.position - b.position);
                this.groupQuestionsBySections();
                this.loading = false;
              }
            });
          },
          error: (err) => {
            console.error('Error al cargar preguntas:', err);
            this.errorMessage = 'Error al cargar las preguntas';
            this.loading = false;
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar secciones:', err);
        this.errorMessage = 'Error al cargar las secciones';
        this.loading = false;
      }
    });
  }

  groupQuestionsBySections(): void {
    // Limpiar agrupación anterior
    this.questionsBySectionId.clear();
    this.sectionOrder = [];

    // Agrupar preguntas por sectionId
    this.questions.forEach(q => {
      const sectionId = q.sectionId;
      if (!this.questionsBySectionId.has(sectionId!)) {
        this.questionsBySectionId.set(sectionId!, []);
      }
      this.questionsBySectionId.get(sectionId!)!.push(q);
    });

    // Agregar preguntas sin sección primero (si existen)
    if (this.questionsBySectionId.has(null)) {
      this.sectionOrder.push(null);
    }

    // Agregar TODAS las secciones en orden, aunque no tengan preguntas
    this.sections.forEach(section => {
      this.sectionOrder.push(section.id);
      // Asegurar que la sección existe en el Map, aunque esté vacía
      if (!this.questionsBySectionId.has(section.id)) {
        this.questionsBySectionId.set(section.id, []);
      }
    });


    this.updateCurrentSectionQuestions();
  }

  updateCurrentSectionQuestions(): void {
    if (this.sectionOrder.length === 0) {
      this.questionsPerPage = [];
      return;
    }

    const currentSectionId = this.sectionOrder[this.currentSectionIndex];
    this.questionsPerPage = this.questionsBySectionId.get(currentSectionId) || [];
  }



  nextSection(): void {
    if (this.currentSectionIndex < this.sectionOrder.length - 1) {
      this.currentSectionIndex++;
      this.updateCurrentSectionQuestions();
    }
  }

  previousSection(): void {
    if (this.currentSectionIndex > 0) {
      this.currentSectionIndex--;
      this.updateCurrentSectionQuestions();
    }
  }

  isLastSection(): boolean {
    return this.currentSectionIndex === this.sectionOrder.length - 1;
  }

  isFirstSection(): boolean {
    return this.currentSectionIndex === 0;
  }

  getCurrentSectionTitle(): string {
    const currentSectionId = this.sectionOrder[this.currentSectionIndex];
    if (currentSectionId === null) {
      return 'Preguntas Generales';
    }
    const section = this.sections.find(s => s.id === currentSectionId);
    return section?.title || 'Sección';
  }

  getProgress(): number {
    if (!this.form || !this.form.progressBar || this.sectionOrder.length === 0) return 0;
    return Math.round(((this.currentSectionIndex + 1) / this.sectionOrder.length) * 100);
  }

  // Manejo de respuestas
  handleChoiceAnswer(questionId: number, optionIndex: number, mode: SelectionMode): void {
    if (mode === SelectionMode.SINGLE) {
      this.userAnswers.set(questionId, optionIndex);
    } else {
      // Múltiple selección
      const current = this.userAnswers.get(questionId) || [];
      const index = current.indexOf(optionIndex);
      if (index > -1) {
        current.splice(index, 1);
      } else {
        current.push(optionIndex);
      }
      this.userAnswers.set(questionId, [...current]);
    }
  }

  isOptionSelected(questionId: number, optionIndex: number, mode: SelectionMode): boolean {
    const answer = this.userAnswers.get(questionId);
    if (mode === SelectionMode.SINGLE) {
      return answer === optionIndex;
    } else {
      return Array.isArray(answer) && answer.includes(optionIndex);
    }
  }

  handleTrueFalseAnswer(questionId: number, value: boolean): void {
    this.userAnswers.set(questionId, value);
  }

  getTrueFalseAnswer(questionId: number): boolean | null {
    return this.userAnswers.get(questionId) ?? null;
  }

  handleTextAnswer(questionId: number, value: string): void {
    this.userAnswers.set(questionId, value);
  }

  getTextAnswer(questionId: number): string {
    return this.userAnswers.get(questionId) || '';
  }

  canSubmit(): boolean {
    // Verificar preguntas requeridas
    const requiredQuestions = this.questions.filter(q => q.required);
    return requiredQuestions.every(q => {
      const answer = this.userAnswers.get(q.id);
      return answer !== undefined && answer !== null && answer !== '';
    });
  }

  async submitPreview(): Promise<void> {
    if (!this.form?.id) {
      alert('ID de formulario no encontrado.');
      return;
    }
    if (!this.isFormOpen()) {
      alert('El formulario está cerrado.');
      return;
    }

    const responsesPayload = Array.from(this.userAnswers.entries()).map(([questionId, value]) => {
      return {
        questionId: Number(questionId),
        value: value
      };
    });

    const r = {
      responses: responsesPayload
    };

    const formId = this.form.id;
    if (!this.formId) {
      alert('ID de formulario no encontrado.');
      return;
    }

    // Determinar el tipo de respondente según el modo del formulario y si hay usuario autenticado
    const email = localStorage.getItem('user_email');
    const isAnonymousForm = this.form.anonymousMode === true;
    const hasUser = !!this.userId;

    // Si no hay usuario y el formulario NO permite anónimos, requerir login
    if (!hasUser && !isAnonymousForm) {
      alert('Debes iniciar sesión para enviar respuestas.');
      return;
    }

    try {
      // Construir el payload según el tipo de respondente
      const submissionPayload: any = {
        formId: formId,
        respondentType: hasUser ? 'USER' : 'ANONYMOUS',
        sourceIp: '127.0.0.1',
        responses: r.responses
      };

      // Agregar campos adicionales solo si hay usuario
      if (hasUser) {
        submissionPayload.userId = this.userId;
        submissionPayload.email = email || undefined;
        submissionPayload.code = null;
      }

      const created = await firstValueFrom(this.submissionService.startSubmissionUser(this.formId, submissionPayload));

      const submissionId = created?.id || created?.submissionId;
      if (!submissionId) {
        console.error('No submission id returned by server', created);
        alert('No se pudo crear la sumisión en el servidor');
        return;
      }

      // Guardar cada respuesta individualmente
      for (const q of this.questions) {
        const answer = this.userAnswers.get(q.id);
        if (answer === undefined || answer === null) continue;

        // Enviar según el tipo de pregunta
        if (q.type === QuestionType.TRUE_FALSE) {
          await firstValueFrom(this.responsesService.saveResponseTrueFalseAnswer(submissionId, q.id, !!answer));
        } else if (q.type === QuestionType.CHOICE) {
          const optionIds: number[] = [];
          if (Array.isArray(answer)) {
            for (const idx of answer) {
              const opt = q.options?.[idx];
              if (opt && opt.id) optionIds.push(opt.id);
            }
          } else {
            const opt = q.options?.[answer];
            if (opt && opt.id) optionIds.push(opt.id);
          }
          if (optionIds.length > 0) {
            await firstValueFrom(this.responsesService.saveResponseChoiceAnswer(submissionId, q.id, optionIds));
          }
        } else {
          const text = String(answer || '');
          await firstValueFrom(this.responsesService.saveResponseTextAnswer(submissionId, q.id, text));
        }
      }
      alert('Respuestas enviadas correctamente');
      this.showModal('successModal');

    } catch (err) {
      console.error('Error al enviar respuestas:', err);
      alert('Error al enviar respuestas');
    }
  }

  showModal(modalId: string): void {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.style.display = 'block';
      try {
        document.body.classList.add('modal-open');

      } catch (e) {
        console.error(e);

      }
    }
  }

  hideModal(modalId?: string): void {
    const id = modalId || 'successModal';
    const modal = document.getElementById(id);
    if (modal) {
      modal.style.display = 'none';
      try {
        document.body.classList.remove('modal-open');

      } catch (e) {
        console.error(e);

      }
    }
  }



  goBack(): void {
    if (!this.formId) {
      window.history.back();
      return;
    }
    if (this.form?.campaignId) {
      this.router.navigate(['/forms', this.formId, 'questions']);
    } else {
      this.router.navigate(['/campaigns']);
    }
  }



  // Estilos dinámicos según el tema
  getPrimaryColor(): string {
    return this.form?.themePrimary || '#3b82f6';
  }

  getThemeMode(): string {
    return this.form?.themeMode || 'light';
  }

  // Helper para convertir selectionMode string a enum
  getSelectionMode(mode: string | undefined): SelectionMode {
    if (mode === 'MULTI') return SelectionMode.MULTI;
    return SelectionMode.SINGLE;
  }


  getTrueLabel(question: QuestionResponse): string {
    if (question.type === QuestionType.TRUE_FALSE && question.options && question.options.length > 0) {
      return question.options[0]?.label || 'Verdadero';
    }
    return 'Verdadero';
  }

  getFalseLabel(question: QuestionResponse): string {
    if (question.type === QuestionType.TRUE_FALSE && question.options && question.options.length > 1) {
      return question.options[1]?.label || 'Falso';
    }
    return 'Falso';
  }

  // Validar estado del formulario (abierto, cerrado, no iniciado)
  checkFormStatus(): void {
    if (!this.form) return;

    const now = new Date();
    const openAt = this.form.openAt ? new Date(this.form.openAt) : null;
    const closeAt = this.form.closeAt ? new Date(this.form.closeAt) : null;

    if (openAt && now < openAt) {
      this.formStatus = 'not-started';
    } else if (closeAt && now > closeAt) {
      this.formStatus = 'closed';
    } else {
      this.formStatus = 'open';
    }
  }

  isFormOpen(): boolean {
    return this.formStatus === 'open';
  }

  getFormStatusMessage(): string {
    const now = new Date();
    const closeAt = this.form?.closeAt ? new Date(this.form.closeAt) : null;
    const openAt = this.form?.openAt ? new Date(this.form.openAt) : null;

    if (this.formStatus === 'not-started') {
      return `El formulario se abrirá el ${openAt?.toLocaleString()}`;
    }
    if (this.formStatus === 'closed') {
      return `El formulario cerró el ${closeAt?.toLocaleString()}`;
    }
    return 'El formulario está abierto';
  }
}
