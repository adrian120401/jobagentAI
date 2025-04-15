export interface IJob {
    jobId: string;
    matchScore: number;
    reason: string;
    job: IJobDetail;
}

export interface IJobDetail {
    jobId: string;
    title: string;
    description: string;
    benefits: string;
    jobUrl: string;
    companyName: string;
    companyLogo: string;
}

export interface IJobRequest {
    message: string;
    jobId: string | null;
}

export interface IJobResponse {
    message: string;
    jobs: IJob[];
}
