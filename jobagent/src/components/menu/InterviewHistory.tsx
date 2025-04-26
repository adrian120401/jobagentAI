import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import InterviewCard from '@/components/InterviewCard';
import { IInterviewResumeDetail } from '@/types/IInterview';
import { useState } from 'react';
import { useEffect } from 'react';
import { Loader2 } from 'lucide-react';
import { getInterviewHistory } from '@/api/user';
interface InterviewHistoryProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
}
const InterviewHistory = ({ isOpen, setIsOpen }: InterviewHistoryProps) => {
    const [interviews, setInterviews] = useState<IInterviewResumeDetail[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const fetchInterviews = async () => {
        setIsLoading(true);
        try {
            const response = await getInterviewHistory();
            setInterviews(response);
        } catch (error) {
            console.error('Error fetching interview history:', error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (isOpen) {
            fetchInterviews();
        }
    }, [isOpen]);

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="md:max-w-2xl lg:max-w-3xl w-[90vw] max-h-[90vh] overflow-y-auto scrollbar">
                <DialogHeader>
                    <DialogTitle>Your Interview History</DialogTitle>
                </DialogHeader>

                {isLoading && (
                    <div className="flex justify-center items-center h-full">
                        <Loader2 className="w-6 h-6 animate-spin" />
                    </div>
                )}

                <div className="flex flex-col gap-4 py-4 ">
                    {interviews.map((interview) => (
                        <InterviewCard key={interview.id} interviewResume={interview} />
                    ))}

                    {interviews.length === 0 && (
                        <p className="text-center text-muted-foreground py-8">
                            No interview history found.
                        </p>
                    )}
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default InterviewHistory;
