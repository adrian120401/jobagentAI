import { useRef, useEffect, useState } from 'react';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetClose } from './ui/sheet';
import { ScrollArea } from './ui/scroll-area';
import { Avatar, AvatarFallback } from './ui/avatar';
import { Button } from './ui/button';
import InputForm from './chat/InputForm';
import { BotMessageSquare, X } from 'lucide-react';
import { useUser } from '@/context/UserContext';
import { IInterview } from '@/types/IInterview';
import { getInterview } from '@/api/chat';

interface InterviewMenuProps {
    isOpen: boolean;
    onClose: () => void;
    jobId?: string;
}

const initialMessages: IInterview[] = [
    {
        question: 'Quieres comenzar la entrevista?',
        step: 0,
    },
];

const InterviewMenu = ({ isOpen, onClose, jobId }: InterviewMenuProps) => {
    const [messages, setMessages] = useState<IInterview[]>(initialMessages);
    const [isLoading, setIsLoading] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const { user } = useUser();
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const closeInterview = () => {
        setMessages(initialMessages);
        setInputValue('');
        onClose();
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!jobId) return;
        if (!inputValue.trim()) return;
        setIsLoading(true);
        try {
            setInputValue('');
            const newMessagesWithAnswer = [...messages];

            newMessagesWithAnswer[newMessagesWithAnswer.length - 1] = {
                ...newMessagesWithAnswer[newMessagesWithAnswer.length - 1],
                answer: inputValue,
            };


            const newMessagesWithFeedback = await getInterview(newMessagesWithAnswer, jobId);

            setMessages([...newMessagesWithAnswer, newMessagesWithFeedback]);
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    return (
        <Sheet open={isOpen} onOpenChange={(open) => !open && onClose()}>
            <SheetContent side="right" className="w-full h-full !max-w-[1280px] flex flex-col p-0">
                <SheetHeader className="flex flex-row items-center justify-between border-b border-border pb-2 px-6 pt-6">
                    <SheetTitle>Interview Mode</SheetTitle>
                    <SheetClose asChild>
                        <Button variant="ghost" size="icon" onClick={closeInterview}>
                            <X className="h-5 w-5" />
                        </Button>
                    </SheetClose>
                </SheetHeader>
                <ScrollArea className="flex-1 px-6 py-4 overflow-y-auto">
                    <div className="space-y-4">
                        {messages.map((msg, idx) => (
                            <div
                                key={idx}
                                className={`flex ${msg.answer ? 'flex-col' : 'justify-start'}`}
                            >
                                {/* Bot question */}
                                <div className="flex items-start gap-2">
                                    <Avatar className="mt-1">
                                        <AvatarFallback className="bg-primary text-primary-foreground">
                                            <BotMessageSquare className="w-4 h-4" />
                                        </AvatarFallback>
                                    </Avatar>
                                    <div className="bg-muted rounded-lg p-3">
                                        {msg.previousFeedback && (
                                            <div className="text-sm text-muted-foreground mt-2">
                                                Feedback: {msg.previousFeedback}
                                            </div>
                                        )}
                                        <div className="font-medium text-lg mt-2">{msg.question}</div>
                                    </div>
                                </div>
                                {/* User answer */}
                                {msg.answer && (
                                    <div className="flex items-start justify-end gap-2 mt-2">
                                        <div className="bg-primary text-primary-foreground rounded-lg p-3">
                                            {msg.answer}
                                        </div>
                                        <Avatar className="mt-1">
                                            <AvatarFallback className="bg-primary text-primary-foreground">
                                                {user?.name?.charAt(0).toUpperCase()}
                                            </AvatarFallback>
                                        </Avatar>
                                    </div>
                                )}
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                        {isLoading && (
                            <div className="flex justify-center items-center py-4">
                                <BotMessageSquare className="w-6 h-6 animate-pulse text-muted-foreground" />
                            </div>
                        )}
                    </div>
                </ScrollArea>
                <div className="p-2">
                    <InputForm
                        inputValue={inputValue}
                        setInputValue={setInputValue}
                        handleSubmit={handleSubmit}
                        className="mb-0"
                        disabled={isLoading}
                    />
                </div>
            </SheetContent>
        </Sheet>
    );
};

export default InterviewMenu;
