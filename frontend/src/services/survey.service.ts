import api from './api';
import type { Survey, SurveySubmitRequest, SurveyResult } from '../types/survey';

/**
 * Survey API 서비스
 */
export const surveyService = {
  /**
   * 설문 목록 조회
   */
  getSurveys: async (): Promise<Survey[]> => {
    const response = await api.get('/api/v1/surveys');
    return response.data;
  },

  /**
   * 설문 상세 조회
   */
  getSurvey: async (surveyId: number): Promise<Survey> => {
    const response = await api.get(`/api/v1/surveys/${surveyId}`);
    return response.data;
  },

  /**
   * 설문 응답 제출
   */
  submitSurvey: async (
    surveyId: number,
    submitData: SurveySubmitRequest
  ): Promise<SurveyResult> => {
    const response = await api.post(
      `/api/v1/surveys/${surveyId}/submit`,
      submitData
    );
    return response.data;
  },
};