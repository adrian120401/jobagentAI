import { IInterviewResume } from '@/types/IInterview';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
interface InterviewResumeProps {
    isOpen: boolean;
    onClose: () => void;
    interviewResume: IInterviewResume | null;
}
const InterviewResume = ({ isOpen, onClose, interviewResume }: InterviewResumeProps) => {
    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Interview Resume</DialogTitle>
                </DialogHeader>
                {interviewResume && (
                    <div className="flex flex-col items-center gap-6 py-4">
                        <div className="flex flex-col items-center gap-2">
                            <div className="relative flex items-center justify-center">
                                {(() => {
                                    const matchPercentage = Math.round(interviewResume.score * 100);
                                    const radius = 40;
                                    const circumference = 2 * Math.PI * radius;
                                    const strokeDashoffset =
                                        circumference - (matchPercentage / 100) * circumference;
                                    const isHighMatch = matchPercentage >= 80;
                                    return (
                                        <svg width="120" height="120">
                                            <circle
                                                cx="60"
                                                cy="60"
                                                r={radius}
                                                stroke="currentColor"
                                                strokeWidth="8"
                                                fill="transparent"
                                                className="text-muted/30"
                                            />
                                            <circle
                                                cx="60"
                                                cy="60"
                                                r={radius}
                                                stroke="currentColor"
                                                strokeWidth="8"
                                                fill="transparent"
                                                strokeDasharray={circumference}
                                                strokeDashoffset={strokeDashoffset}
                                                strokeLinecap="round"
                                                className={
                                                    isHighMatch
                                                        ? 'text-green-500'
                                                        : 'text-amber-500'
                                                }
                                                transform="rotate(-90 60 60)"
                                            />
                                            <text
                                                x="50%"
                                                y="50%"
                                                textAnchor="middle"
                                                dominantBaseline="middle"
                                                fontSize="2.5rem"
                                                fontWeight="bold"
                                                fill={isHighMatch ? '#22c55e' : '#f59e42'}
                                            >
                                                {matchPercentage}
                                            </text>
                                        </svg>
                                    );
                                })()}
                            </div>
                            <span className="text-lg font-semibold text-muted-foreground mt-2">
                                Score
                            </span>
                        </div>
                        <div className="w-full max-w-md bg-card border border-border/40 rounded-lg shadow p-6 flex flex-col items-center">
                            <span className="text-base font-medium text-primary mb-2">
                                General Feedback
                            </span>
                            <p className="text-sm text-muted-foreground text-center whitespace-pre-line">
                                {interviewResume.feedback || 'No feedback available.'}
                            </p>
                        </div>
                    </div>
                )}
            </DialogContent>
        </Dialog>
    );
};

export default InterviewResume;
