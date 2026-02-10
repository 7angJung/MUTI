import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/auth.service';
import { useAuthStore } from '../../store/authStore';

export default function LoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await authService.login(formData);

      // TODO: 백엔드에서 사용자 정보를 함께 반환하도록 수정 필요
      // 임시로 토큰만 저장
      setAuth(
        { id: 0, email: formData.email, nickname: '', createdAt: '' },
        response.accessToken,
        response.refreshToken
      );

      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || '로그인에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-spotify-black via-spotify-gray-dark to-spotify-black py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        {/* Logo */}
        <div className="text-center">
          <h1 className="text-4xl font-bold text-white mb-2">
            <span className="text-spotify-green">MU</span>TI
          </h1>
          <p className="text-spotify-gray-light">
            계정에 로그인하세요
          </p>
        </div>

        {/* Form */}
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4">
            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-spotify-gray-light mb-2">
                이메일
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-3 bg-spotify-gray-dark border border-spotify-border rounded-lg text-white placeholder-spotify-gray-light focus:outline-none focus:border-spotify-green focus:ring-1 focus:ring-spotify-green transition-colors"
                placeholder="your@email.com"
              />
            </div>

            {/* Password */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-spotify-gray-light mb-2">
                비밀번호
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                value={formData.password}
                onChange={handleChange}
                className="w-full px-4 py-3 bg-spotify-gray-dark border border-spotify-border rounded-lg text-white placeholder-spotify-gray-light focus:outline-none focus:border-spotify-green focus:ring-1 focus:ring-spotify-green transition-colors"
                placeholder="••••••••"
              />
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="bg-red-500 bg-opacity-10 border border-red-500 text-red-500 px-4 py-3 rounded-lg text-sm">
              {error}
            </div>
          )}

          {/* Submit Button */}
          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-3 px-4 rounded-full bg-spotify-green text-white font-bold hover:bg-spotify-green-light hover:scale-105 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
          >
            {isLoading ? '로그인 중...' : '로그인'}
          </button>

          {/* Divider */}
          <div className="relative my-6">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-spotify-border"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-spotify-black text-spotify-gray-light">또는</span>
            </div>
          </div>

          {/* Sign Up Link */}
          <div className="text-center">
            <p className="text-spotify-gray-light">
              계정이 없으신가요?{' '}
              <Link
                to="/signup"
                className="text-spotify-green hover:text-spotify-green-light font-semibold transition-colors"
              >
                회원가입
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
}