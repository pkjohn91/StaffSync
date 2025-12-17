import axios from 'axios';

/**
 * Axios 인스턴스 생성 및 인터셉터 설정
 */
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 모든 요청에 JWT 토큰 자동 첨부
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 401 에러 시 로그인 페이지로 리다이렉트
api.interceptors.response.use(
  (response) => {return response;},
  (error) => {
    if (error.response && error.response.status === 401) {
      // 토큰 만료 또는 인증 실패
      console.log('인증 실패: 로그인 페이지로 이동');

      // localStorage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');

      // 로그인 페이지 리다이렉트
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;