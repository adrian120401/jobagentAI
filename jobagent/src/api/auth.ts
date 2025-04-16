import { LoginResponse } from '@/types/IUser';
import { fetchApi } from './config';

export async function login(email: string, password: string): Promise<LoginResponse> {
    return fetchApi<LoginResponse>('/users/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
        public: true,
    });
}
