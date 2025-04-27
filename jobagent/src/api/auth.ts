import { LoginResponse } from '@/types/IUser';
import { fetchApi } from './config';

export async function login(email: string, password: string): Promise<LoginResponse> {
    return fetchApi<LoginResponse>('/users/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
        public: true,
    });
}

export async function register(email: string, password: string, name: string): Promise<LoginResponse> {
    return fetchApi<LoginResponse>('/users/register', {
        method: 'POST',
        body: JSON.stringify({ email, password, name }),
        public: true,
    });
}