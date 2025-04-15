import { fetchApi } from './config';
import { IJobRequest, IJobResponse } from '@/types/IJob';

export async function getMessage(request: IJobRequest): Promise<IJobResponse> {
    return fetchApi<IJobResponse>('/chats', {
        method: 'POST',
        body: JSON.stringify(request),
    });
}
