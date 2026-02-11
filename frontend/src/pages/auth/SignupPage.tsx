import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/auth.service';
import { useAuthStore } from '../../store/authStore';

export default function SignupPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();

  const [formData, setFormData] = useState({
    email: '',
    username: '',
    password: '',
    confirmPassword: '',
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

  const validateForm = () => {
    if (!formData.email || !formData.username || !formData.password) {
      setError('모든 필드를 입력해주세요.');
      return false;
    }

    // 비밀번호: 8~20자, 영문+숫자+특수문자 필수
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;
    if (!passwordRegex.test(formData.password)) {
      setError('비밀번호는 8~20자이며 영문, 숫자, 특수문자(@$!%*#?&)를 포함해야 합니다.');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('올바른 이메일 형식이 아닙니다.');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      const response = await authService.signup({
        email: formData.email,
        username: formData.username,
        password: formData.password,
      });

      // TODO: 백엔드에서 사용자 정보를 함께 반환하도록 수정 필요
      // 임시로 토큰만 저장
      setAuth(
          { id: 0, email: formData.email, nickname: formData.username, createdAt: '' },
        response.accessToken,
        response.refreshToken
      );

      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
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
            무료로 가입하고 음악을 즐기세요
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

            {/* Nickname */}
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-spotify-gray-light mb-2">
                닉네임
              </label>
              <input
                id="username"
                name="username"
                type="text"
                required
                value={formData.username}
                onChange={handleChange}
                className="w-full px-4 py-3 bg-spotify-gray-dark border border-spotify-border rounded-lg text-white placeholder-spotify-gray-light focus:outline-none focus:border-spotify-green focus:ring-1 focus:ring-spotify-green transition-colors"
                placeholder="닉네임을 입력하세요"
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
                placeholder="최소 8자 이상"
              />
            </div>

            {/* Confirm Password */}
            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-spotify-gray-light mb-2">
                비밀번호 확인
              </label>
              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                required
                value={formData.confirmPassword}
                onChange={handleChange}
                className="w-full px-4 py-3 bg-spotify-gray-dark border border-spotify-border rounded-lg text-white placeholder-spotify-gray-light focus:outline-none focus:border-spotify-green focus:ring-1 focus:ring-spotify-green transition-colors"
                placeholder="비밀번호를 다시 입력하세요"
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
            {isLoading ? '가입 중...' : '회원가입'}
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

          {/* Login Link */}
          <div className="text-center">
            <p className="text-spotify-gray-light">
              이미 계정이 있으신가요?{' '}
              <Link
                to="/login"
                className="text-spotify-green hover:text-spotify-green-light font-semibold transition-colors"
              >
                로그인
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
}