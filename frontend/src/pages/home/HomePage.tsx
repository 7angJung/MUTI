import { Link } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';

export default function HomePage() {
  const { isAuthenticated, user } = useAuthStore();

  return (
    <div className="min-h-screen bg-gradient-to-b from-spotify-black via-spotify-gray-dark to-spotify-black">
      {/* Hero Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center">
          <h1 className="text-5xl md:text-7xl font-bold text-white mb-6">
            음악으로 세상과
            <span className="text-spotify-green"> 연결</span>되다
          </h1>
          <p className="text-xl text-spotify-gray-light mb-12 max-w-2xl mx-auto">
            MUTI는 음악을 통해 사람들을 연결하는 소셜 플랫폼입니다.
            당신의 플레이리스트를 공유하고, 새로운 음악을 발견하세요.
          </p>

          {isAuthenticated ? (
            <div className="space-y-4">
              <p className="text-spotify-green text-lg">
                환영합니다, {user?.nickname || user?.email}님!
              </p>
              <div className="flex gap-4 justify-center">
                <Link
                  to="/playlists"
                  className="px-8 py-4 rounded-full bg-spotify-green text-white font-bold hover:bg-spotify-green-light hover:scale-105 transition-all duration-200"
                >
                  내 플레이리스트
                </Link>
                <Link
                  to="/discover"
                  className="px-8 py-4 rounded-full bg-transparent border-2 border-white text-white font-bold hover:bg-white hover:text-spotify-black transition-all duration-200"
                >
                  음악 발견하기
                </Link>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              <Link
                to="/survey"
                className="inline-block px-12 py-5 rounded-full bg-spotify-green text-white font-bold text-lg hover:bg-spotify-green-light hover:scale-105 transition-all duration-200 shadow-lg"
              >
                🎵 음악 성향 테스트 시작하기
              </Link>
              <div className="flex gap-4 justify-center">
                <Link
                  to="/signup"
                  className="px-6 py-3 rounded-full bg-transparent border-2 border-spotify-green text-spotify-green font-bold hover:bg-spotify-green hover:text-white transition-all duration-200"
                >
                  회원가입
                </Link>
                <Link
                  to="/login"
                  className="px-6 py-3 rounded-full bg-transparent border-2 border-white text-white font-bold hover:bg-white hover:text-spotify-black transition-all duration-200"
                >
                  로그인
                </Link>
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Features Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <h2 className="text-3xl md:text-4xl font-bold text-white text-center mb-16">
          MUTI의 특별함
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Feature 1 */}
          <div className="bg-spotify-gray-dark p-8 rounded-lg hover:bg-opacity-80 transition-all duration-200">
            <div className="text-4xl mb-4">🎵</div>
            <h3 className="text-xl font-bold text-white mb-4">플레이리스트 공유</h3>
            <p className="text-spotify-gray-light">
              나만의 플레이리스트를 만들고 친구들과 공유하세요.
            </p>
          </div>

          {/* Feature 2 */}
          <div className="bg-spotify-gray-dark p-8 rounded-lg hover:bg-opacity-80 transition-all duration-200">
            <div className="text-4xl mb-4">🎧</div>
            <h3 className="text-xl font-bold text-white mb-4">음악 발견</h3>
            <p className="text-spotify-gray-light">
              새로운 아티스트와 트렌디한 곡을 발견하세요.
            </p>
          </div>

          {/* Feature 3 */}
          <div className="bg-spotify-gray-dark p-8 rounded-lg hover:bg-opacity-80 transition-all duration-200">
            <div className="text-4xl mb-4">👥</div>
            <h3 className="text-xl font-bold text-white mb-4">소셜 연결</h3>
            <p className="text-spotify-gray-light">
              음악 취향이 비슷한 사람들과 연결되세요.
            </p>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      {!isAuthenticated && (
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="bg-gradient-to-r from-spotify-green to-spotify-green-light rounded-2xl p-12 text-center">
            <h2 className="text-3xl md:text-4xl font-bold text-white mb-6">
              지금 바로 시작하세요
            </h2>
            <p className="text-white text-lg mb-8 opacity-90">
              무료로 가입하고 음악의 세계로 빠져보세요.
            </p>
            <Link
              to="/signup"
              className="inline-block px-12 py-4 rounded-full bg-white text-spotify-black font-bold hover:scale-105 transition-all duration-200"
            >
              회원가입
            </Link>
          </div>
        </section>
      )}
    </div>
  );
}