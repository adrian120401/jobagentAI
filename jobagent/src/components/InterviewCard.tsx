import { IInterviewResumeDetail } from '@/types/IInterview';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ExternalLink } from 'lucide-react';

interface InterviewCardProps {
    interviewResume: IInterviewResumeDetail;
}

const InterviewCard = ({ interviewResume }: InterviewCardProps) => {
    const { job, score, feedback } = interviewResume;
    const matchPercentage = Math.round(score * 100);
    const isHighMatch = matchPercentage >= 80;

    const radius = 18;
    const circumference = 2 * Math.PI * radius;
    const strokeDashoffset = circumference - (matchPercentage / 100) * circumference;

    return (
        <Card className="overflow-hidden bg-card hover:shadow-md transition-all duration-300 border border-border/40 pt-6 pb-0">
            <div className="flex flex-col md:flex-row px-2 gap-4">
                <div className="w-[90px] h-[90px] bg-white flex-shrink-0 flex items-center justify-center rounded-md mx-auto md:mx-0">
                    <img
                        src={job.companyLogo}
                        alt={job.companyName}
                        className="max-w-[80%] max-h-[80%] object-contain"
                    />
                </div>

                <div className="flex-1 px-4">
                    <div className="mb-1.5">
                        <span className="text-primary font-semibold">{job.title}</span>
                    </div>

                    <div className="mb-2">
                        <span className="text-sm text-muted-foreground">{job.companyName}</span>
                    </div>

                    <p className="text-sm text-muted-foreground">{feedback}</p>
                </div>

                <div className="w-20 flex-shrink-0 flex items-center justify-center relative border-l border-border/40 mx-auto md:mx-0">
                    <div className="relative flex items-center justify-center">
                        <svg width="80" height="80" className="transform -rotate-90">
                            <circle
                                cx="40"
                                cy="40"
                                r={radius}
                                stroke="currentColor"
                                strokeWidth="3"
                                fill="transparent"
                                className="text-muted/30"
                            />
                            <circle
                                cx="40"
                                cy="40"
                                r={radius}
                                stroke="currentColor"
                                strokeWidth="3"
                                fill="transparent"
                                strokeDasharray={circumference}
                                strokeDashoffset={strokeDashoffset}
                                strokeLinecap="round"
                                className={isHighMatch ? 'text-green-500' : 'text-amber-500'}
                            />
                        </svg>

                        <div className="absolute inset-0 flex items-center justify-center">
                            <span
                                className={`text-sm font-bold ${
                                    isHighMatch ? 'text-green-500' : 'text-amber-500'
                                }`}
                            >
                                {matchPercentage}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="border-t border-border/40">
                <Button
                    variant="default"
                    className="w-full rounded-none text-xs font-medium h-12 bg-primary/90 hover:bg-primary"
                    asChild
                >
                    <a
                        href={job.jobUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center justify-center gap-1.5"
                    >
                        View Job
                        <ExternalLink size={14} />
                    </a>
                </Button>
            </div>
        </Card>
    );
};

export default InterviewCard;
