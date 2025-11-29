import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { QuestionService } from '../../../Services/question.service';
import { SectionService } from '../../../Services/section.service';
import { FormService } from '../../../Services/form.service';
import { PermissionService } from '../../../Services/permission.service';
import { finalize } from 'rxjs/operators';
import {
  QuestionResponse,
  QuestionType,
  QuestionFormData,
  SelectionMode,
  TextMode
} from '../../../Models/question.model';
import { SectionResponse } from '../../../Models/section.model';
import { Forms } from '../../../Models/form.model';
import { MemberRole } from '../../../Models/member.model';
import { PublishResponse } from '../../../Models/Publish.model';

@Component({
  selector: 'app-question-builder',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './question-builder.component.html',
  styleUrls: ['./question-builder.component.css']
})
export class QuestionBuilderComponent implements OnInit {
  // IDs y datos del formulario
  formId!: number;
  form: Forms | null = null;
  userRole: MemberRole | null = null;

  // Listas
  sections: SectionResponse[] = [];
  questions: QuestionResponse[] = [];

  // Estado de UI
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Modal de nueva pregunta
  showNewQuestionModal = false;
  selectedQuestionType: QuestionType | null = null;

  // Edición
  editingQuestion: QuestionResponse | null = null;
  showEditModal = false;

  // Form data para nueva/editar pregunta
  questionFormData: QuestionFormData = this.getEmptyFormData();

  // Sección seleccionada para filtrar
  selectedSectionId: number | null = null;

  // Enums para el template
  QuestionType = QuestionType;
  SelectionMode = SelectionMode;
  TextMode = TextMode;

  // Publish
  showPublishModal = false;
  formPublishedLink: string = '';
  



  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private questionService: QuestionService,
    private sectionService: SectionService,
    private formService: FormService,
    private permissionService: PermissionService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('formId');
    if (id) {
      this.formId = +id;
      this.loadFormData();
      this.loadSections();
      this.loadQuestions();
    } else {
      this.errorMessage = 'ID de formulario no encontrado';
    }
  }

  loadPermissions(campaignId: number): void {
    this.permissionService.getUserRoleInCampaign(campaignId).subscribe({
      next: (role) => {
        this.userRole = role;
      },
      error: () => {
        this.userRole = null;
      }
    });
  }

  // ============================================
  // LOAD DATA
  // ============================================

  loadFormData(): void {
    this.formService.getFormById(this.formId).subscribe({
      next: (form) => {
        this.form = form;
        this.loadPermissions(form.campaignId);
      },
      error: (err) => {
        console.error('Error al cargar formulario:', err);
        this.errorMessage = 'Error al cargar el formulario';
      }
    });
  }



  loadSections(): void {
    this.sectionService.getSectionsByForm(this.formId).subscribe({
      next: (sections) => {
        this.sections = sections;
      },
      error: (err) => {
        console.error('Error al cargar secciones:', err);
      }
    });
  }

  loadQuestions(): void {
    this.loading = true;
    this.questionService.getQuestionsByForm(
      this.formId,
      this.selectedSectionId || undefined
    ).subscribe({
      next: (questions) => {
        this.questions = questions.sort((a, b) => a.position - b.position);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar preguntas:', err);
        this.errorMessage = 'Error al cargar las preguntas';
        this.loading = false;
      }
    });
  }

  // ============================================
  // SECTIONS
  // ============================================

  createSection(): void {
    if (!this.canManageQuestions()) {
      this.errorMessage = 'No tienes permisos para crear secciones';
      return;
    }
    const title = prompt('Nombre de la nueva sección:');
    if (title && title.trim()) {
      this.sectionService.addSection(this.formId, title.trim()).subscribe({
        next: () => {
          this.loadSections();
          this.showSuccess('Sección creada exitosamente');
        },
        error: (err) => {
          this.errorMessage = 'Error al crear sección';
          console.error(err);
        }
      });
    }
  }

  public openModalPublish(): void {
    if (!this.canManageQuestions()) {
      this.errorMessage = 'No tienes permisos para publicar el formulario';
      return;
    }
    this.showPublishModal = true;
    
  }

  public publishForm(): void {
    if (!this.form) {
      this.errorMessage = 'Formulario no cargado';
      return;
    }

    this.loading = true;
    this.formService.publishForm(this.formId, false).pipe(
  
    ).subscribe({
      
      next: (response: PublishResponse) => {
        const url = `${window.location.origin}/public/forms/${response.code}`;
        this.formPublishedLink = url;
        this.showSuccess('Formulario publicado exitosamente');
        this.loading = false;
        this.loadFormData();
      },

      error: (err) => {
        this.errorMessage = 'Error al publicar el formulario';
        console.error(err);
      }
    });
  }
  

  closeModalPublish(): void {
    this.showPublishModal = false;
  }

  copyToClipboard(text: string): void {
    navigator.clipboard.writeText(text).then(() => {
      this.showSuccess('Enlace copiado al portapapeles');
    }).catch(() => {
      this.errorMessage = 'Error al copiar el enlace';
    });
  }

  deleteSection(section: SectionResponse): void {
    if (!this.canDeleteQuestions()) {
      this.errorMessage = 'No tienes permisos para eliminar secciones';
      return;
    }
    if (confirm(`¿Eliminar la sección "${section.title}"? Las preguntas se moverán a "Sin sección".`)) {
      this.sectionService.deleteSection(this.formId, section.id!).subscribe({
        next: () => {
          this.loadSections();
          this.loadQuestions();
          this.showSuccess('Sección eliminada');
        },
        error: (err) => {
          this.errorMessage = 'Error al eliminar sección';
          console.error(err);
        }
      });
    }
  }

  filterBySection(sectionId: number | null): void {
    this.selectedSectionId = sectionId;
    this.loadQuestions();
  }

  // ============================================
  // NEW QUESTION MODAL
  // ============================================

  openNewQuestionModal(type: QuestionType): void {
    if (!this.canManageQuestions()) {
      this.errorMessage = 'No tienes permisos para crear preguntas';
      return;
    }
    this.selectedQuestionType = type;
    this.questionFormData = this.getEmptyFormData();
    this.questionFormData.type = type;

    // Valores por defecto según tipo
    if (type === QuestionType.CHOICE) {
      this.questionFormData.selectionMode = SelectionMode.SINGLE;
      this.questionFormData.options = [
        { label: 'Opción 1', correct: false },
        { label: 'Opción 2', correct: false }
      ];
    } else if (type === QuestionType.TEXT) {
      this.questionFormData.textMode = TextMode.SHORT;
    } else if (type === QuestionType.TRUE_FALSE) {
      this.questionFormData.trueLabel = 'Verdadero';
      this.questionFormData.falseLabel = 'Falso';
      this.questionFormData.trueIsCorrect = false;
    } else if (type === QuestionType.MATCHING) {
      this.questionFormData.leftTexts = ['', ''];
      this.questionFormData.rightTexts = ['', ''];
      this.questionFormData.keyPairs = [
        { leftIndex: 0, rightIndex: 0 },
        { leftIndex: 1, rightIndex: 1 }
      ];
    }

    this.showNewQuestionModal = true;
  }

  closeNewQuestionModal(): void {
    this.showNewQuestionModal = false;
    this.selectedQuestionType = null;
    this.questionFormData = this.getEmptyFormData();
  }

  // ============================================
  // CREATE QUESTION
  // ============================================

  saveNewQuestion(): void {
    if (!this.validateQuestionForm()) {
      return;
    }

    this.loading = true;
    this.questionService.createQuestion(
      this.formId,
      this.selectedSectionId,
      this.questionFormData
    ).subscribe({
      next: () => {
        this.showSuccess('Pregunta creada exitosamente');
        this.closeNewQuestionModal();
        this.loadQuestions();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al crear la pregunta: ' + (err.error?.message || err.message);
        this.loading = false;
        console.error(err);
      }
    });
  }

  // ============================================
  // EDIT QUESTION
  // ============================================

  openEditQuestion(question: QuestionResponse): void {
    if (!this.canManageQuestions()) {
      this.errorMessage = 'No tienes permisos para editar preguntas';
      return;
    }
    this.editingQuestion = question;
    // Cargar datos en el form
    this.questionFormData = {
      type: question.type,
      prompt: question.prompt,
      helpText: question.helpText,
      required: question.required,
      shuffleOptions: question.shuffleOptions,
      selectionMode: question.selectionMode as SelectionMode,
      minSelections: question.minSelections,
      maxSelections: question.maxSelections,
      options: question.options ? [...question.options] : [],
      textMode: question.textMode as TextMode,
      placeholder: question.placeholder,
      minLength: question.minLength,
      maxLength: question.maxLength
    };
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingQuestion = null;
  }

  saveEditQuestion(): void {
    if (!this.editingQuestion || !this.validateQuestionForm()) {
      return;
    }

    const questionId = this.editingQuestion.id;

    // Actualizar campos básicos si cambiaron
    const updates: any[] = [];

    if (this.questionFormData.prompt !== this.editingQuestion.prompt) {
      updates.push(this.questionService.updatePrompt(questionId, this.questionFormData.prompt));
    }
    if (this.questionFormData.helpText !== this.editingQuestion.helpText) {
      updates.push(this.questionService.updateHelp(questionId, this.questionFormData.helpText));
    }
    if (this.questionFormData.required !== this.editingQuestion.required) {
      updates.push(this.questionService.setRequired(questionId, this.questionFormData.required));
    }
    if (this.questionFormData.shuffleOptions !== this.editingQuestion.shuffleOptions) {
      updates.push(this.questionService.setShuffle(questionId, this.questionFormData.shuffleOptions));
    }

    // Actualizar opciones para CHOICE/TRUE_FALSE
    if (this.questionFormData.type === QuestionType.CHOICE && this.questionFormData.options) {
      updates.push(this.questionService.updateOptions(questionId, this.questionFormData.options));

      if (this.questionFormData.selectionMode === SelectionMode.MULTI) {
        updates.push(this.questionService.setBounds(
          questionId,
          this.questionFormData.minSelections,
          this.questionFormData.maxSelections
        ));
      }
    }

    // Actualizar settings de TEXT
    if (this.questionFormData.type === QuestionType.TEXT) {
      updates.push(this.questionService.updateTextSettings(
        questionId,
        this.questionFormData.textMode!,
        this.questionFormData.placeholder,
        this.questionFormData.minLength,
        this.questionFormData.maxLength
      ));
    }

    if (updates.length === 0) {
      this.showSuccess('No se detectaron cambios');
      this.closeEditModal();
      return;
    }

    this.loading = true;
    // Ejecutar todas las actualizaciones
    Promise.all(updates.map(obs => obs.toPromise()))
      .then(() => {
        this.showSuccess('Pregunta actualizada');
        this.closeEditModal();
        this.loadQuestions();
        this.loading = false;
      })
      .catch(err => {
        this.errorMessage = 'Error al actualizar pregunta';
        this.loading = false;
        console.error(err);
      });
  }

  formReady(): void {
    this.openNewQuestionModal(QuestionType.CHOICE);
  }

  // ============================================
  // DELETE QUESTION
  // ============================================

  deleteQuestion(question: QuestionResponse): void {
    if (!this.canDeleteQuestions()) {
      this.errorMessage = 'No tienes permisos para eliminar preguntas';
      return;
    }
    if (confirm(`¿Eliminar la pregunta "${question.prompt}"?`)) {
      this.questionService.deleteQuestion(question.id).subscribe({
        next: () => {
          this.showSuccess('Pregunta eliminada');
          this.loadQuestions();
        },
        error: (err) => {
          this.errorMessage = 'Error al eliminar pregunta';
          console.error(err);
        }
      });
    }
  }

  // ============================================
  // HELPERS - OPTIONS (CHOICE)
  // ============================================

  addOption(): void {
    if (!this.questionFormData.options) {
      this.questionFormData.options = [];
    }
    this.questionFormData.options.push({
      label: `Opción ${this.questionFormData.options.length + 1}`,
      correct: false
    });
  }

  removeOption(index: number): void {
    if (this.questionFormData.options && this.questionFormData.options.length > 2) {
      this.questionFormData.options.splice(index, 1);
    }
  }

  // ============================================
  // HELPERS - MATCHING
  // ============================================

  addMatchingPair(): void {
    if (!this.questionFormData.leftTexts) this.questionFormData.leftTexts = [];
    if (!this.questionFormData.rightTexts) this.questionFormData.rightTexts = [];
    if (!this.questionFormData.keyPairs) this.questionFormData.keyPairs = [];

    const index = this.questionFormData.leftTexts.length;
    this.questionFormData.leftTexts.push('');
    this.questionFormData.rightTexts.push('');
    this.questionFormData.keyPairs.push({ leftIndex: index, rightIndex: index });
  }

  removeMatchingPair(index: number): void {
    if (this.questionFormData.leftTexts && this.questionFormData.leftTexts.length > 1) {
      this.questionFormData.leftTexts.splice(index, 1);
      this.questionFormData.rightTexts!.splice(index, 1);
      this.questionFormData.keyPairs!.splice(index, 1);
    }
  }

  // ============================================
  // VALIDATION
  // ============================================

  validateQuestionForm(): boolean {
    if (!this.questionFormData.prompt || this.questionFormData.prompt.trim().length < 5) {
      this.errorMessage = 'La pregunta debe tener al menos 5 caracteres';
      return false;
    }

    if (this.questionFormData.type === QuestionType.CHOICE) {
      if (!this.questionFormData.options || this.questionFormData.options.length < 2) {
        this.errorMessage = 'Debe haber al menos 2 opciones';
        return false;
      }
      const hasEmpty = this.questionFormData.options.some(o => !o.label.trim());
      if (hasEmpty) {
        this.errorMessage = 'Todas las opciones deben tener texto';
        return false;
      }
    }

    if (this.questionFormData.type === QuestionType.MATCHING) {
      if (!this.questionFormData.leftTexts || this.questionFormData.leftTexts.length < 1) {
        this.errorMessage = 'Debe haber al menos 1 par de relación';
        return false;
      }
      const hasEmpty = this.questionFormData.leftTexts.some(t => !t.trim()) ||
        this.questionFormData.rightTexts!.some(t => !t.trim());
      if (hasEmpty) {
        this.errorMessage = 'Todos los elementos deben tener texto';
        return false;
      }
    }

    this.errorMessage = '';
    return true;
  }

  // ============================================
  // UTILITIES
  // ============================================

  getEmptyFormData(): QuestionFormData {
    return {
      type: QuestionType.CHOICE,
      prompt: '',
      helpText: '',
      required: false,
      shuffleOptions: false
    };
  }

  showSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }

  getQuestionTypeLabel(type: QuestionType): string {
    return this.questionService.getQuestionTypeLabel(type);
  }

  getQuestionTypeIcon(type: QuestionType): string {
    return this.questionService.getQuestionTypeIcon(type);
  }

  goBack(): void {
    if (this.form?.campaignId) {
      this.router.navigate(['/campaigns', this.form.campaignId, 'forms']);
    } else {
      this.router.navigate(['/campaigns']);
    }
  }

  canManageQuestions(): boolean {
    return this.permissionService.canManageQuestions(this.userRole);
  }

  canDeleteQuestions(): boolean {
    return this.permissionService.canDeleteQuestions(this.userRole);
  }
}
