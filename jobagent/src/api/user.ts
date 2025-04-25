import { fetchApiWithFile } from './config';
import { IUserFile } from '@/types/IUser';
export async function uploadCV(file: File): Promise<IUserFile> {
    const formData = new FormData();
    formData.append('file', file);
    return fetchApiWithFile<IUserFile>('/users/cv', {
        method: 'POST',
        body: formData,
        public: false,
    });
}

export async function uploadDocx(file: File): Promise<IUserFile> {
    const formData = new FormData();
    formData.append('file', file);
    return fetchApiWithFile<IUserFile>('/users/docx', {
        method: 'POST',
        body: formData,
        public: false,
    });
}
