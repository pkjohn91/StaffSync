import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axiosConfig';
// import axios from 'axios';

const ProductEditPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    minStockLevel: 0,
    price: 0
  });
  const [productInfo, setProductInfo] = useState(null); // 읽기 전용 정보 (재고, 상태 등)
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const categories = ['전자제품', '가구', '문구', '식품', '의류', '기타'];

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await api.get(`http://localhost:8080/api/products/${id}`);
        const product = response.data;
        
        setProductInfo({
          quantity: product.quantity,
          status: product.status
        });
        
        setFormData({
          name: product.name,
          category: product.category,
          minStockLevel: product.minStockLevel,
          price: product.price
        });
      } catch (error) {
        alert('상품 정보를 불러오는데 실패했습니다: ' + (error.response?.data || error.message));
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: (name === 'name' || name === 'category') ? value : Number(value)
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      alert('상품명을 입력해주세요.');
      return;
    }
    if (formData.minStockLevel < 0 || formData.price < 0) {
      alert('값은 0 이상이어야 합니다.');
      return;
    }

    try {
      setSaving(true);
      // 참고: 백엔드에 PUT /api/products/{id} 엔드포인트가 상품 메타데이터 수정을 지원해야 함
      await api.put(`http://localhost:8080/api/products/${id}`, formData);
      alert('✅ 상품 정보가 수정되었습니다!');
      navigate('/dashboard');
    } catch (error)
      {
      alert('수정 실패: ' + (error.response?.data || error.message));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-8 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-xl font-bold text-gray-900">상품 정보 수정</h1>
            <p className="text-xs text-gray-500">{formData.name}</p>
          </div>
          <button onClick={() => navigate('/dashboard')} className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700">
            대시보드로 돌아가기
          </button>
        </div>
      </nav>

      <div className="max-w-3xl mx-auto p-8">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-6">
            <p className="font-medium text-blue-800">읽기 전용 정보</p>
            <p className="text-blue-700 mt-1">
              <span className="font-semibold">현재 재고:</span> {productInfo?.quantity}개 |
              <span className="font-semibold ml-3">재고 상태:</span> {productInfo?.status}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">상품명</label>
              <input type="text" name="name" value={formData.name} onChange={handleChange} required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500" />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">카테고리</label>
              <select name="category" value={formData.category} onChange={handleChange} required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500">
                <option value="">카테고리 선택</option>
                {categories.map(cat => <option key={cat} value={cat}>{cat}</option>)}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">최소 재고 수준</label>
                    <input type="number" name="minStockLevel" value={formData.minStockLevel} onChange={handleChange} min="0" required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500" />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">가격 (₩)</label>
                    <input type="number" name="price" value={formData.price} onChange={handleChange} min="0" required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500" />
                </div>
            </div>

            <div className="flex justify-end gap-4 pt-4">
              <button type="button" onClick={() => navigate('/dashboard')}
                className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50">
                취소
              </button>
              <button type="submit" disabled={saving}
                className="px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50">
                {saving ? '저장 중...' : '변경사항 저장'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ProductEditPage;
