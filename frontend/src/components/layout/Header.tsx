import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { authService } from '../../services/auth.service';

export default function Header() {
  const { isAuthenticated, user, clearAuth } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    authService.logout();
    clearAuth();
    navigate('/login');
  };

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-spotify-black border-b border-spotify-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="text-2xl font-bold text-white">
              <span className="text-spotify-green">MU</span>TI
            </div>
          </Link>

          {/* Navigation */}
          <nav className="flex items-center space-x-6">
            {isAuthenticated ? (
              <>
                <span className="text-spotify-gray-light text-sm">
                  {user?.nickname || user?.email}
                </span>
                <button
                  onClick={handleLogout}
                  className="px-6 py-2 rounded-full bg-transparent border border-spotify-gray-light text-white hover:border-white hover:scale-105 transition-all duration-200"
                >
                  로그아웃
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="text-spotify-gray-light hover:text-white transition-colors"
                >
                  로그인
                </Link>
                <Link
                  to="/signup"
                  className="px-6 py-2 rounded-full bg-white text-spotify-black font-semibold hover:scale-105 transition-all duration-200"
                >
                  회원가입
                </Link>
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
}