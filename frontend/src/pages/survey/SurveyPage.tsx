import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { surveyService } from '../../services/survey.service';
import type { Survey, SurveyAnswer } from '../../types/survey';

export default function SurveyPage() {
  const navigate = useNavigate();
  const [survey, setSurvey] = useState<Survey | null>(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<SurveyAnswer[]>([]);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 설문 데이터 로드
  useEffect(() => {
    loadSurvey();
  }, []);

  const loadSurvey = async () => {
    try {
      setLoading(true);
      // 첫 번째 설문 조회 (ID: 1)
      const data = await surveyService.getSurvey(1);
      console.log('Survey data received:', data);

      // 데이터 검증
      if (!data.questions || data.questions.length === 0) {
        setError('설문에 질문이 없습니다.');
        return;
      }

      setSurvey(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || '설문을 불러오는데 실패했습니다.');
      console.error('설문 로드 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 다음 질문으로 이동
  const handleNext = () => {
    if (selectedOption === null) {
      alert('선택지를 선택해주세요!');
      return;
    }

    // 응답 저장
    const currentQuestion = survey!.questions[currentQuestionIndex];
    const newAnswer: SurveyAnswer = {
      questionId: currentQuestion.id,
      optionId: selectedOption,
    };

    const updatedAnswers = [...answers, newAnswer];
    setAnswers(updatedAnswers);
    setSelectedOption(null);

    // 마지막 질문인 경우 제출
    if (currentQuestionIndex === survey!.questions.length - 1) {
      submitSurvey(updatedAnswers);
    } else {
      // 다음 질문으로
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  // 이전 질문으로 이동
  const handlePrevious = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
      // 이전 응답 제거
      const updatedAnswers = answers.slice(0, -1);
      setAnswers(updatedAnswers);
      // 이전 선택 복원
      const previousAnswer = answers[answers.length - 1];
      setSelectedOption(previousAnswer?.optionId || null);
    }
  };

  // 설문 제출
  const submitSurvey = async (finalAnswers: SurveyAnswer[]) => {
    try {
      const result = await surveyService.submitSurvey(survey!.id, {
        surveyId: survey!.id,
        answers: finalAnswers,
      });

      // 결과 페이지로 이동 (결과 데이터 전달)
      navigate('/survey/result', { state: { result } });
    } catch (err: any) {
      setError(err.response?.data?.message || '설문 제출에 실패했습니다.');
      console.error('설문 제출 실패:', err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-spotify-black via-spotify-gray-dark to-spotify-black flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-spotify-green mx-auto mb-4"></div>
          <p className="text-spotify-gray-light text-lg">설문을 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error || !survey || !survey.questions || survey.questions.length === 0) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-spotify-black via-spotify-gray-dark to-spotify-black flex items-center justify-center">
        <div className="text-center max-w-md mx-auto px-4">
          <div className="text-6xl mb-6">😢</div>
          <h2 className="text-2xl font-bold text-white mb-4">문제가 발생했습니다</h2>
          <p className="text-spotify-gray-light mb-8">{error || '설문 데이터를 불러올 수 없습니다.'}</p>
          <button
            onClick={loadSurvey}
            className="px-8 py-3 rounded-full bg-spotify-green text-white font-bold hover:bg-spotify-green-light transition-all duration-200"
          >
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  const currentQuestion = survey.questions[currentQuestionIndex];
  const progress = ((currentQuestionIndex + 1) / survey.questions.length) * 100;

  return (
    <div className="min-h-screen bg-gradient-to-b from-spotify-black via-spotify-gray-dark to-spotify-black">
      <div className="max-w-3xl mx-auto px-4 py-12">
        {/* 헤더 */}
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-bold text-white mb-4">
            {survey.title}
          </h1>
          <p className="text-spotify-gray-light text-lg">{survey.description}</p>
        </div>

        {/* 진행률 바 */}
        <div className="mb-8">
          <div className="flex justify-between items-center mb-2">
            <span className="text-spotify-gray-light text-sm">
              질문 {currentQuestionIndex + 1} / {survey.questions.length}
            </span>
            <span className="text-spotify-green text-sm font-bold">
              {Math.round(progress)}%
            </span>
          </div>
          <div className="w-full bg-spotify-gray-dark rounded-full h-2 overflow-hidden">
            <div
              className="bg-spotify-green h-full rounded-full transition-all duration-500 ease-out"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        {/* 질문 카드 */}
        <div className="bg-spotify-gray-dark rounded-2xl p-8 md:p-12 mb-8 shadow-2xl">
          {/* 질문 */}
          <h2 className="text-2xl md:text-3xl font-bold text-white mb-8 leading-relaxed">
            {currentQuestion.content}
          </h2>

          {/* 선택지 */}
          <div className="space-y-4">
            {currentQuestion.options.map((option) => (
              <button
                key={option.id}
                onClick={() => setSelectedOption(option.id)}
                className={`w-full text-left p-6 rounded-xl border-2 transition-all duration-200 ${
                  selectedOption === option.id
                    ? 'border-spotify-green bg-spotify-green bg-opacity-10 scale-[1.02]'
                    : 'border-spotify-gray-light border-opacity-20 hover:border-spotify-green hover:border-opacity-50 hover:bg-spotify-gray-dark hover:bg-opacity-50'
                }`}
              >
                <div className="flex items-center">
                  <div
                    className={`w-6 h-6 rounded-full border-2 mr-4 flex items-center justify-center ${
                      selectedOption === option.id
                        ? 'border-spotify-green bg-spotify-green'
                        : 'border-spotify-gray-light'
                    }`}
                  >
                    {selectedOption === option.id && (
                      <div className="w-3 h-3 bg-white rounded-full" />
                    )}
                  </div>
                  <span
                    className={`text-lg ${
                      selectedOption === option.id
                        ? 'text-white font-semibold'
                        : 'text-spotify-gray-light'
                    }`}
                  >
                    {option.content}
                  </span>
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* 버튼 영역 */}
        <div className="flex justify-between items-center">
          <button
            onClick={handlePrevious}
            disabled={currentQuestionIndex === 0}
            className={`px-8 py-4 rounded-full font-bold transition-all duration-200 ${
              currentQuestionIndex === 0
                ? 'bg-spotify-gray-dark text-spotify-gray-light cursor-not-allowed opacity-50'
                : 'bg-transparent border-2 border-white text-white hover:bg-white hover:text-spotify-black'
            }`}
          >
            ← 이전
          </button>

          <button
            onClick={handleNext}
            disabled={selectedOption === null}
            className={`px-8 py-4 rounded-full font-bold transition-all duration-200 ${
              selectedOption === null
                ? 'bg-spotify-gray-light text-spotify-gray-dark cursor-not-allowed opacity-50'
                : 'bg-spotify-green text-white hover:bg-spotify-green-light hover:scale-105'
            }`}
          >
            {currentQuestionIndex === survey.questions.length - 1 ? '완료' : '다음 →'}
          </button>
        </div>
      </div>
    </div>
  );
}