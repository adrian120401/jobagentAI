import { IJobDetail } from "./IJob";

export interface IInterview {
    question: string;
    answer?: string;
    previousFeedback?: string;
    step: number;
    interview?: IInterviewResume;
}

export interface IInterviewResume {
    id: string;
    score: number;
    feedback: string;
}

export interface IInterviewResumeDetail extends IInterviewResume {
    job: IJobDetail;
}
