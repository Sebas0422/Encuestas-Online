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

  // Respuestas del usuario (simuladas)
  userAnswers: Map<number, any> = new Map();

  // Estado de paginación
  currentPage = 0;
  questionsPerPage: QuestionResponse[] = [];

  // Enums para el template
  QuestionType = QuestionType;
  SelectionMode = SelectionMode;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formService: FormService,
    private questionService: QuestionService,
    private sectionService: SectionService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('formId');
    if (id) {
      this.formId = +id;
      this.loadFormData();
      this.loadQuestions();
    } else {
      this.errorMessage = 'ID de formulario no encontrado';
    }
  }

  loadFormData(): void {
    this.loading = true;
    this.formService.getFormById(this.formId).subscribe({
      next: (form) => {
        this.form = form;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar formulario:', err);
        this.errorMessage = 'Error al cargar el formulario';
        this.loading = false;
      }
    });
  }

  loadQuestions(): void {
    this.questionService.getQuestionsByForm(this.formId).subscribe({
      next: (questions) => {
        this.questions = questions.sort((a, b) => a.position - b.position);
        this.updatePagination();
      },
      error: (err) => {
        console.error('Error al cargar preguntas:', err);
        this.errorMessage = 'Error al cargar las preguntas';
      }
    });
  }

  updatePagination(): void {
    if (!this.form) return;

    if (this.form.paginated) {
      // Mostrar una pregunta por página
      this.questionsPerPage = this.questions.slice(this.currentPage, this.currentPage + 1);
    } else {
      // Mostrar todas las preguntas
      this.questionsPerPage = this.questions;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.questions.length - 1) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  getProgress(): number {
    if (!this.form || !this.form.progressBar || this.questions.length === 0) return 0;
    return Math.round(((this.currentPage + 1) / this.questions.length) * 100);
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

  submitPreview(): void {
    alert('Esta es una vista previa. En el formulario real, las respuestas se enviarían al servidor.');
  }

  goBack(): void {
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

  // Helper para obtener labels de true/false
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
}
