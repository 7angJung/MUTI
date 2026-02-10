// 설문 관련 타입 정의

export interface QuestionOption {
  id: number;
  content: string;
  score: number;
  dimensionType: 'E_I' | 'S_F' | 'A_D' | 'P_U';
  dimensionValue: 'E' | 'I' | 'S' | 'F' | 'A' | 'D' | 'P' | 'U';
}

export interface Question {
  id: number;
  content: string;
  questionOrder: number;
  dimensionType: 'E_I' | 'S_F' | 'A_D' | 'P_U';
  options: QuestionOption[];
}

export interface Survey {
  id: number;
  title: string;
  description: string;
  questions: Question[];
}

export interface SurveyAnswer {
  questionId: number;
  optionId: number;
}

export interface SurveySubmitRequest {
  surveyId: number;
  answers: SurveyAnswer[];
  sessionId?: string;
  userId?: number;
}

export interface SurveyResult {
  id: number;
  surveyId: number;
  mutiType: string;
  mutiTypeName: string;
  mutiTypeDescription: string;
  axisScores: {
    E_I: number;
    S_F: number;
    A_D: number;
    P_U: number;
  };
  axisDirections: {
    E_I: string;
    S_F: string;
    A_D: string;
    P_U: string;
  };
  createdAt: string;
}