import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResponsesListComponent } from '../reponses-list/responses-list.component';

describe('ResponsesListComponent', () => {
  let component: ResponsesListComponent;
  let fixture: ComponentFixture<ResponsesListComponent>;  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResponsesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
