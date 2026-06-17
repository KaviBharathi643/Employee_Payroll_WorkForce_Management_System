import axiosClient from '../api/axiosClient';
import { unwrapWithMessage } from '../api/apiResponse';

const profileService = {
  uploadPhoto(file) {
    const formData = new FormData();
    formData.append('profilePhoto', file);
    return axiosClient
      .post('/api/profile/photo', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then(unwrapWithMessage);
  },
  deletePhoto() {
    return axiosClient.delete('/api/profile/photo').then(unwrapWithMessage);
  },
  fetchPhotoBlob(userId) {
    return axiosClient.get(`/api/profile/photo/${userId}`, {
      responseType: 'blob',
    });
  },
};

export default profileService;
