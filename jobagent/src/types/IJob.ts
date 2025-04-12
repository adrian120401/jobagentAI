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
