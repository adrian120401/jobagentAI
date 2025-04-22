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
