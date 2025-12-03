import { Component, OnInit, AfterViewInit, OnDestroy, ElementRef, ViewChildren, QueryList } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ChartConfiguration, ChartType, Chart } from 'chart.js';
import { forkJoin } from 'rxjs';
import { FormService } from '../../../Services/form.service';
import { QuestionService } from '../../../Services/question.service';
import { SectionService } from '../../../Services/section.service';
import { ResponsesService } from '../../../Services/responses.service';
import { Forms } from '../../../Models/form.model';
import { QuestionResponse, QuestionType } from '../../../Models/question.model';

type ChartTypeOption = 'pie' | 'doughnut' | 'table';

interface QuestionStats {
  question: QuestionResponse;
  stats: any;
  chartType: ChartTypeOption;
}

@Component({
  selector: 'app-responses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './responses-list.component.html',
  styleUrls: ['./responses-list.component.css']
})
export class ResponsesListComponent implements OnInit {
  formId!: number;
  form: Forms | null = null;
  questions: QuestionResponse[] = [];
  submissions: any[] = [];

  loading = false;
  errorMessage = '';

  questionStats: QuestionStats[] = [];

  QuestionType = QuestionType;
  @ViewChildren('chartCanvas') chartCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;
  private charts: Chart[] = [];

  // Para los gráficos
  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: true,
    plugins: {
      legend: {
        display: true,
        position: 'top'
      }
    }
  };

  constructor(
    private route: ActivatedRoute,
    private formService: FormService,
    private questionService: QuestionService,
    private sectionService: SectionService,
    private responsesService: ResponsesService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('formId');
    if (id) {
      this.formId = +id;
      this.loadData();
    }
  }

  loadData(): void {
    this.loading = true;
    this.formService.getFormById(this.formId).subscribe({
      next: (form) => {
        this.form = form;
        this.loadQuestions();
      },
      error: (err) => {
        console.error('Error al cargar formulario:', err);
        this.errorMessage = 'Error al cargar el formulario';
        this.loading = false;
      }
    });
  }

  loadQuestions(): void {
    // Primero cargar las secciones
    this.sectionService.getSectionsByForm(this.formId).subscribe({
      next: (sections) => {
        // Cargar preguntas sin sección
        this.questionService.getQuestionsByForm(this.formId, undefined).subscribe({
          next: (questionsWithoutSection) => {
            // Cargar preguntas de cada sección
            const sectionQueries = sections.map(section =>
              this.questionService.getQuestionsByForm(this.formId, section.id)
            );

            if (sectionQueries.length === 0) {
              // Solo hay preguntas sin sección
              this.questions = questionsWithoutSection.sort((a, b) => a.position - b.position);
              this.loadSubmissions();
              return;
            }

            // Usar forkJoin para cargar todas las secciones en paralelo
            forkJoin(sectionQueries).subscribe({
              next: (sectionQuestionsArrays) => {
                const allSectionQuestions = sectionQuestionsArrays.flat();
                this.questions = [...questionsWithoutSection, ...allSectionQuestions]
                  .sort((a, b) => a.position - b.position);
                console.log('📊 Total de preguntas cargadas para estadísticas:', this.questions.length);
                this.loadSubmissions();
              },
              error: (err) => {
                console.error('Error al cargar preguntas de secciones:', err);
                this.questions = questionsWithoutSection.sort((a, b) => a.position - b.position);
                this.loadSubmissions();
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
        // Si falla cargar secciones, intentar cargar solo preguntas sin sección
        this.questionService.getQuestionsByForm(this.formId, undefined).subscribe({
          next: (questions) => {
            this.questions = questions.sort((a, b) => a.position - b.position);
            this.loadSubmissions();
          },
          error: (err2) => {
            console.error('Error al cargar preguntas (fallback):', err2);
            this.errorMessage = 'Error al cargar las preguntas';
            this.loading = false;
          }
        });
      }
    });
  }

  loadSubmissions(): void {
    this.responsesService.getSubmissionsByForm(this.formId).subscribe({
      next: (data: any) => {
        this.submissions = data.items || data;
        this.loadReportData();
      },
      error: (err) => {
        console.error('Error al cargar respuestas:', err);
        this.errorMessage = 'Error al cargar las respuestas';
        this.loading = false;
      }
    });
  }

  loadReportData(): void {
    this.responsesService.getFormReport(this.formId, true).subscribe({
      next: (report: any) => {
        this.processStatsFromReport(report);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar reporte:', err);
        this.responsesService.getFormReport(this.formId, false).subscribe({
          next: (report: any) => {
            this.processStatsFromReport(report);
            this.loading = false;
          },
          error: (err2) => {
            console.error('Error al cargar reporte (fallback):', err2);
            this.processStats([]);
            this.loading = false;
          }
        });
      }
    });
  }



  processStatsFromReport(report: any): void {
    this.questionStats = this.questions.map(q => {
      const questionReport = report.questions?.find((qr: any) => qr.questionId === q.id);
      const stats = this.calculateStatsFromReport(q, questionReport);
      return {
        question: q,
        stats: stats,
        chartType: this.getDefaultChartType(q)
      };
    });
    setTimeout(() => this.renderCharts(), 0);
  }

  calculateStatsFromReport(question: QuestionResponse, questionReport: any = {}): any {
    if (question.type === QuestionType.CHOICE) {
      return this.statsForChoiceFromReport(question, questionReport);
    } else if (question.type === QuestionType.TRUE_FALSE) {
      return this.statsForTrueFalseFromReport(question, questionReport);
    } else if (question.type === QuestionType.TEXT) {
      return this.statsForTextFromReport(question, questionReport);
    }
    return {};
  }

  statsForChoiceFromReport(question: QuestionResponse, questionReport: any = {}): any {
    const counts: { [key: string]: number } = {};
    (question.options || []).forEach(opt => {
      counts[opt.label] = 0;
    });

    let totalCount = 0;
    if (questionReport.options) {
      questionReport.options.forEach((optReport: any) => {
        const option = question.options?.find(o => o.id === optReport.optionId);
        if (option) {
          counts[option.label] = optReport.count || 0;
          totalCount += optReport.count || 0;
        }
      });
    }

    if (totalCount === 0 && questionReport.answeredCount && questionReport.answeredCount > 0) {
      const perOption = Math.round(questionReport.answeredCount / Object.keys(counts).length);
      const remainder = questionReport.answeredCount % Object.keys(counts).length;
      const labels = Object.keys(counts);
      labels.forEach((label, idx) => {
        counts[label] = perOption + (idx < remainder ? 1 : 0);
      });
    }

    return {
      labels: (question.options || []).map(opt => opt.label),
      data: Object.values(counts),
      total: questionReport.answeredCount || 0,
      hasDataIssue: totalCount === 0 && questionReport.answeredCount > 0
    };
  }

  statsForTrueFalseFromReport(question: QuestionResponse, questionReport: any = {}): any {
    const trueLabel = (question.options?.[0]?.label || 'Verdadero');
    const falseLabel = (question.options?.[1]?.label || 'Falso');

    const trueCount = questionReport.trueCount || 0;
    const falseCount = questionReport.falseCount || 0;
    const totalCount = trueCount + falseCount;

    let finalTrueCount = trueCount;
    let finalFalseCount = falseCount;
    let hasDataIssue = false;

    if (totalCount === 0 && questionReport.answeredCount && questionReport.answeredCount > 0) {
      const half = Math.floor(questionReport.answeredCount / 2);
      finalTrueCount = half;
      finalFalseCount = questionReport.answeredCount - half;
      hasDataIssue = true;
    }

    return {
      labels: [trueLabel, falseLabel],
      data: [finalTrueCount, finalFalseCount],
      total: questionReport.answeredCount || 0,
      hasDataIssue: hasDataIssue
    };
  }

  statsForTextFromReport(question: QuestionResponse, questionReport: any = {}): any {
    return {
      responses: [],
      total: questionReport.answeredCount || 0,
      omitted: questionReport.omittedCount || 0
    };
  }

  processStats(responsesData: any[]): void {
    this.questionStats = this.questions.map((q, idx) => {
      const responses = responsesData[idx] || [];
      const stats = this.calculateStats(q, responses);
      return {
        question: q,
        stats: stats,
        chartType: this.getDefaultChartType(q)
      };
    });
    setTimeout(() => this.renderCharts(), 0);
  }

  calculateStats(question: QuestionResponse, responses: any[] = []): any {
    if (question.type === QuestionType.CHOICE) {
      return this.statsForChoice(question, responses);
    } else if (question.type === QuestionType.TRUE_FALSE) {
      return this.statsForTrueFalse(question, responses);
    } else if (question.type === QuestionType.TEXT) {
      return this.statsForText(question, responses);
    }
    return {};
  }

  statsForChoice(question: QuestionResponse, responses: any[] = []): any {
    const counts: { [key: string]: number } = {};
    (question.options || []).forEach(opt => {
      counts[opt.label] = 0;
    });

    responses.forEach((resp: any) => {
      if (resp.answers) {
        resp.answers.forEach((answer: any) => {
          const optionIds = answer.optionIds || (answer.optionId ? [answer.optionId] : []);
          optionIds.forEach((optId: number) => {
            const option = question.options?.find(o => o.id === optId);
            if (option) {
              counts[option.label]++;
            }
          });
        });
      }
    });

    return {
      labels: (question.options || []).map(opt => opt.label),
      data: Object.values(counts),
      total: this.submissions.length
    };
  }

  statsForTrueFalse(question: QuestionResponse, responses: any[] = []): any {
    const trueLabel = (question.options?.[0]?.label || 'Verdadero');
    const falseLabel = (question.options?.[1]?.label || 'Falso');

    let trueCount = 0;
    let falseCount = 0;

    responses.forEach((resp: any) => {
      if (resp.answers) {
        resp.answers.forEach((answer: any) => {
          if (answer.value === true) trueCount++;
          else if (answer.value === false) falseCount++;
        });
      }
    });

    return {
      labels: [trueLabel, falseLabel],
      data: [trueCount, falseCount],
      total: this.submissions.length
    };
  }

  statsForText(question: QuestionResponse, responses: any[] = []): any {
    const textResponses: string[] = [];

    responses.forEach((resp: any) => {
      if (resp.answers) {
        resp.answers.forEach((answer: any) => {
          if (answer.value && typeof answer.value === 'string') {
            textResponses.push(answer.value);
          }
        });
      }
    });

    return {
      responses: textResponses,
      total: this.submissions.length
    };
  }

  getDefaultChartType(question: QuestionResponse): ChartTypeOption {
    if (question.type === QuestionType.CHOICE) return 'pie';
    if (question.type === QuestionType.TRUE_FALSE) return 'pie';
    if (question.type === QuestionType.TEXT) return 'table';
    return 'table';
  }

  changeChartType(index: number, chartType: ChartTypeOption): void {
    if (this.questionStats[index]) {
      this.questionStats[index].chartType = chartType;
      setTimeout(() => this.renderCharts(), 0);
    }
  }

  getChartData(stat: any): ChartConfiguration['data'] | null {
    if (!stat.labels) return null;

    return {
      labels: stat.labels,
      datasets: [
        {
          label: 'Respuestas',
          data: stat.data,
          backgroundColor: this.getColors(stat.data.length),
          borderColor: this.getColors(stat.data.length, true),
          borderWidth: 1
        }
      ]
    };
  }

  getColors(count: number, isBorder = false): string[] {
    const colors = [
      '#3b82f6', '#ef4444', '#10b981', '#f59e0b',
      '#8b5cf6', '#ec4899', '#14b8a6', '#f97316'
    ];
    return Array(count).fill(0).map((_, i) => {
      if (isBorder) return colors[i % colors.length].replace('#', '#') + 'cc';
      return colors[i % colors.length];
    });
  }

  getTotalResponses(): number {
    return this.submissions.filter(s => s.status === 'SUBMITTED' || s.answersCount > 0).length;
  }

  getCompletionRate(): number {
    if (this.submissions.length === 0) return 0;
    const completed = this.submissions.filter(s => s.status === 'SUBMITTED').length;
    return Math.round((completed / this.submissions.length) * 100);
  }

  private renderCharts(): void {
    this.charts.forEach(c => c.destroy());
    this.charts = [];

    const canvases = this.chartCanvases ? this.chartCanvases.toArray() : [];
    let canvasIndex = 0;

    this.questionStats.forEach((qStat) => {
      if (qStat.chartType === 'table') return;

      const data = this.getChartData(qStat.stats);
      if (!data) return;

      const canvasRef = canvases[canvasIndex];
      canvasIndex++;

      if (!canvasRef) return;

      const ctx = canvasRef.nativeElement.getContext('2d');
      if (!ctx) return;

      const config: ChartConfiguration = {
        type: qStat.chartType as ChartType,
        data: data,
        options: this.chartOptions,

      } as ChartConfiguration;

      const chart = new Chart(ctx, config as any);
      this.charts.push(chart);
    });
  }

  ngOnDestroy(): void {
    this.charts.forEach(c => c.destroy());
    this.charts = [];
  }
}
