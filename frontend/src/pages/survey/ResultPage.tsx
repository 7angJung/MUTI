import { useLocation, useNavigate, Link } from 'react-router-dom';
import type { SurveyResult } from '../../types/survey';

export default function ResultPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const result = location.state?.result as SurveyResult;

  // 결과 데이터가 없으면 홈으로 리다이렉트
  if (!result) {
    navigate('/');
    return null;
  }

  // 음악 타입별 설명
  const typeDescriptions: Record<string, { title: string; description: string; emoji: string }> = {
    ESAP: {
      title: '열정적인 파티 메이커',
      description: '활기차고 감각적이며 분석적인 당신! 파티의 중심에서 모두를 즐겁게 만드는 타입입니다.',
      emoji: '🎉',
    },
    ESAU: {
      title: '자유로운 영혼',
      description: '외향적이고 감각적이며 독립적인 당신! 규칙에 얽매이지 않고 자유롭게 음악을 즐깁니다.',
      emoji: '🦋',
    },
    ESDP: {
      title: '계획형 DJ',
      description: '체계적이고 디테일한 플레이리스트를 만드는 당신! 모든 순간에 완벽한 음악을 준비합니다.',
      emoji: '🎧',
    },
    ESDU: {
      title: '즉흥 음악 탐험가',
      description: '감각적이고 독립적인 당신! 그 순간의 느낌에 따라 자유롭게 음악을 선택합니다.',
      emoji: '🌟',
    },
    EFAP: {
      title: '감성 큐레이터',
      description: '감정이 풍부하고 분석적인 당신! 무드에 맞는 완벽한 플레이리스트를 만듭니다.',
      emoji: '💖',
    },
    EFAU: {
      title: '감성 예술가',
      description: '직관적이고 자유로운 당신! 음악으로 감정을 표현하는 것을 좋아합니다.',
      emoji: '🎨',
    },
    EFDP: {
      title: '무드 메이커',
      description: '계획적이고 감성적인 당신! 모든 상황에 어울리는 음악을 준비합니다.',
      emoji: '✨',
    },
    EFDU: {
      title: '감성 자유인',
      description: '자유롭고 감성적인 당신! 그 순간의 감정에 따라 음악을 선택합니다.',
      emoji: '🌈',
    },
    ISAP: {
      title: '조용한 분석가',
      description: '차분하고 분석적인 당신! 혼자서 음악의 깊은 의미를 탐구합니다.',
      emoji: '📚',
    },
    ISAU: {
      title: '독립적 탐험가',
      description: '독립적이고 감각적인 당신! 혼자만의 음악 세계를 만들어갑니다.',
      emoji: '🎯',
    },
    ISDP: {
      title: '완벽주의 청취자',
      description: '디테일하고 계획적인 당신! 완벽한 음질과 순서로 음악을 듣습니다.',
      emoji: '🎼',
    },
    ISDU: {
      title: '자유로운 몽상가',
      description: '독립적이고 즉흥적인 당신! 자신만의 리듬으로 음악을 즐깁니다.',
      emoji: '☁️',
    },
    IFAP: {
      title: '감성 철학자',
      description: '깊이 있고 분석적인 당신! 음악의 감성적 의미를 탐구합니다.',
      emoji: '🌙',
    },
    IFAU: {
      title: '내면의 예술가',
      description: '감성적이고 자유로운 당신! 음악으로 내면의 세계를 표현합니다.',
      emoji: '🎭',
    },
    IFDP: {
      title: '감성 완벽주의자',
      description: '계획적이고 감성적인 당신! 완벽한 무드의 플레이리스트를 만듭니다.',
      emoji: '💫',
    },
    IFDU: {
      title: '자유로운 감성인',
      description: '독립적이고 감성적인 당신! 제약 없이 음악을 느끼고 즐깁니다.',
      emoji: '🦄',
    },
  };

  const typeInfo = typeDescriptions[result.mutiType] || {
    title: result.mutiTypeName || '음악 애호가',
    description: result.mutiTypeDescription || '음악을 사랑하는 당신의 독특한 성향을 발견했습니다!',
    emoji: '🎵',
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-spotify-black via-spotify-gray-dark to-spotify-black">
      <div className="max-w-4xl mx-auto px-4 py-12">
        {/* 결과 헤더 */}
        <div className="text-center mb-12">
          <div className="text-8xl mb-6 animate-bounce">{typeInfo.emoji}</div>
          <h1 className="text-5xl md:text-6xl font-bold text-white mb-4">
            당신의 음악 타입은
          </h1>
          <div className="text-6xl md:text-7xl font-bold text-spotify-green mb-6">
            {result.mutiType}
          </div>
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
            {typeInfo.title}
          </h2>
          <p className="text-xl text-spotify-gray-light max-w-2xl mx-auto">
            {typeInfo.description}
          </p>
        </div>

        {/* 성향 분석 카드 */}
        <div className="bg-spotify-gray-dark rounded-2xl p-8 mb-8 shadow-2xl">
          <h3 className="text-2xl font-bold text-white mb-6">성향 분석</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* E/I */}
            <div className="bg-spotify-black rounded-lg p-6">
              <div className="flex justify-between items-center mb-3">
                <span className="text-spotify-gray-light">에너지 방향</span>
                <span className="text-spotify-green font-bold">
                  {result.axisDirections.E_I}
                </span>
              </div>
              <div className="text-white font-semibold">
                {result.axisDirections.E_I === 'E' ? '외향적 (Extroverted)' : '내향적 (Introverted)'}
              </div>
              <p className="text-spotify-gray-light text-sm mt-2">
                {result.axisDirections.E_I === 'E'
                  ? '함께 음악을 즐기는 것을 좋아합니다'
                  : '혼자 음악을 감상하는 것을 선호합니다'}
              </p>
            </div>

            {/* S/F */}
            <div className="bg-spotify-black rounded-lg p-6">
              <div className="flex justify-between items-center mb-3">
                <span className="text-spotify-gray-light">감상 방식</span>
                <span className="text-spotify-green font-bold">
                  {result.axisDirections.S_F}
                </span>
              </div>
              <div className="text-white font-semibold">
                {result.axisDirections.S_F === 'S' ? '감각적 (Sensory)' : '감성적 (Feeling)'}
              </div>
              <p className="text-spotify-gray-light text-sm mt-2">
                {result.axisDirections.S_F === 'S'
                  ? '리듬과 비트를 중시합니다'
                  : '가사와 감정을 중시합니다'}
              </p>
            </div>

            {/* A/D */}
            <div className="bg-spotify-black rounded-lg p-6">
              <div className="flex justify-between items-center mb-3">
                <span className="text-spotify-gray-light">선곡 스타일</span>
                <span className="text-spotify-green font-bold">
                  {result.axisDirections.A_D}
                </span>
              </div>
              <div className="text-white font-semibold">
                {result.axisDirections.A_D === 'A' ? '분석적 (Analytical)' : '디테일 (Detail)'}
              </div>
              <p className="text-spotify-gray-light text-sm mt-2">
                {result.axisDirections.A_D === 'A'
                  ? '음악의 의미를 분석합니다'
                  : '음악의 세부사항을 중시합니다'}
              </p>
            </div>

            {/* P/U */}
            <div className="bg-spotify-black rounded-lg p-6">
              <div className="flex justify-between items-center mb-3">
                <span className="text-spotify-gray-light">플레이리스트</span>
                <span className="text-spotify-green font-bold">
                  {result.axisDirections.P_U}
                </span>
              </div>
              <div className="text-white font-semibold">
                {result.axisDirections.P_U === 'P' ? '계획적 (Planned)' : '자유로운 (Unplanned)'}
              </div>
              <p className="text-spotify-gray-light text-sm mt-2">
                {result.axisDirections.P_U === 'P'
                  ? '체계적인 플레이리스트를 만듭니다'
                  : '즉흥적으로 음악을 선택합니다'}
              </p>
            </div>
          </div>
        </div>

        {/* CTA 버튼 */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link
            to="/signup"
            className="px-8 py-4 rounded-full bg-spotify-green text-white font-bold text-center hover:bg-spotify-green-light hover:scale-105 transition-all duration-200"
          >
            회원가입하고 플레이리스트 만들기
          </Link>
          <Link
            to="/"
            className="px-8 py-4 rounded-full bg-transparent border-2 border-white text-white font-bold text-center hover:bg-white hover:text-spotify-black transition-all duration-200"
          >
            홈으로 돌아가기
          </Link>
        </div>

        {/* 공유 섹션 */}
        <div className="mt-12 text-center">
          <p className="text-spotify-gray-light mb-4">친구들과 결과를 공유해보세요!</p>
          <div className="flex gap-4 justify-center">
            <button
              onClick={() => {
                const text = `나의 음악 타입은 ${result.mutiType} - ${typeInfo.title}! 🎵`;
                if (navigator.share) {
                  navigator.share({ text });
                } else {
                  navigator.clipboard.writeText(text);
                  alert('클립보드에 복사되었습니다!');
                }
              }}
              className="px-6 py-3 rounded-full bg-spotify-gray-dark text-white hover:bg-opacity-80 transition-all duration-200"
            >
              📋 결과 공유하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}