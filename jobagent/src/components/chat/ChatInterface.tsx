import { useState, useRef, useEffect } from 'react';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { ScrollArea } from '@/components/ui/scroll-area';
import { IJob } from '@/types/IJob';
import { JobCard } from '@/components/JobCard';
import InputForm from './InputForm';

import { BotMessageSquare, Loader2 } from 'lucide-react';
import { Navbar } from '../navbar/Navbar';
import { useUser } from '@/context/UserContext';
export type MessageContent = string | IJob[];

export interface Message {
    id: string;
    content: MessageContent;
    isUser: boolean;
}

interface ChatInterfaceProps {
    messages: Message[];
    onSendMessage: (message: string) => void;
    isLoadingMessage: boolean;
}

const ChatInterface = ({ messages, onSendMessage, isLoadingMessage }: ChatInterfaceProps) => {
    const [inputValue, setInputValue] = useState('');
    const { user } = useUser();

    const messagesEndRef = useRef<HTMLDivElement>(null);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!inputValue.trim()) return;

        onSendMessage(inputValue);
        setInputValue('');
    };

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const renderMessageContent = (content: MessageContent) => {
        if (typeof content === 'string') {
            return (
                <div
                    className="chat-content flex flex-col gap-2"
                    dangerouslySetInnerHTML={{ __html: content }}
                />
            );
        } else if (Array.isArray(content)) {
            return (
                <div className="space-y-4">
                    {content.map((job) => (
                        <JobCard key={job.jobId} job={job} />
                    ))}
                </div>
            );
        }
        return null;
    };

    return (
        <div className="flex flex-col h-full">
            <Navbar />

            <div className="flex flex-col justify-between flex-1 w-full max-w-4xl mx-auto overflow-y-auto">
                <ScrollArea className="flex-1 overflow-y-auto">
                    <div className="p-4 space-y-4">
                        {messages.map((message) => (
                            <div
                                key={message.id}
                                className={`flex ${
                                    message.isUser ? 'justify-end' : 'justify-start'
                                }`}
                            >
                                {!message.isUser && (
                                    <Avatar className="mr-2 flex-shrink-0 mt-1">
                                        <AvatarFallback className="bg-primary text-primary-foreground">
                                            <BotMessageSquare className="w-4 h-4" />
                                        </AvatarFallback>
                                    </Avatar>
                                )}
                                <div
                                    className={`max-w-[80%] p-3 rounded-lg ${
                                        message.isUser
                                            ? 'bg-primary text-primary-foreground rounded-br-none'
                                            : 'bg-muted rounded-bl-none'
                                    }`}
                                >
                                    {renderMessageContent(message.content)}
                                </div>
                                {message.isUser && (
                                    <Avatar className="ml-2 flex-shrink-0 mt-1">
                                        <AvatarFallback className="bg-primary text-primary-foreground">
                                            {user?.name?.charAt(0).toUpperCase()}
                                        </AvatarFallback>
                                    </Avatar>
                                )}
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                        {isLoadingMessage && (
                            <div className="flex justify-center items-center">
                                <Loader2 className="w-8 h-8 animate-spin" />
                            </div>
                        )}
                    </div>
                </ScrollArea>

                <InputForm
                    inputValue={inputValue}
                    setInputValue={setInputValue}
                    handleSubmit={handleSubmit}
                />
            </div>
        </div>
    );
};

export default ChatInterface;
