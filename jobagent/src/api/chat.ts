import { IInterview } from '@/types/IInterview';
import { fetchApi } from './config';
import { IJobRequest, IJobResponse } from '@/types/IJob';

export async function getMessage(request: IJobRequest): Promise<IJobResponse> {
    return fetchApi<IJobResponse>('/chats', {
        method: 'POST',
        body: JSON.stringify(request),
        public: false,
    });
}

export async function getInterview(history: IInterview[], jobId: string): Promise<IInterview> {
    console.log(history);
    return fetchApi<IInterview>('/chats/interview', {
        method: 'POST',
        body: JSON.stringify({ history, jobId }),
        public: false,
    });
}
