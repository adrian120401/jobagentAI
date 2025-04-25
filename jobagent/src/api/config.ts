export const API_BASE_URL = 'http://localhost:8080';

interface FetchOptions extends RequestInit {
    public?: boolean;
}

export async function fetchApi<T>(endpoint: string, options: FetchOptions = {}): Promise<T> {
    const { public: isPublic, ...fetchOptions } = options;

    const headers = new Headers(options.headers);
    headers.set('Content-Type', 'application/json');

    if (!isPublic) {
        const token = localStorage.getItem('token');
        if (token) {
            headers.set('Authorization', `Bearer ${token}`);
        }
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...fetchOptions,
        headers,
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
    }

    return response.json();
}

export async function fetchApiWithFile<T>(
    endpoint: string,
    options: FetchOptions = {}
): Promise<T> {
    const { public: isPublic, ...fetchOptions } = options;

    const headers = new Headers(options.headers);

    if (!isPublic) {
        const token = localStorage.getItem('token');
        if (token) {
            headers.set('Authorization', `Bearer ${token}`);
        }
    }

    console.log(headers);

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...fetchOptions,
        headers,
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
    }

    return response.json();
}
