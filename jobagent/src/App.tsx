import { useState } from 'react';
import ChatInterface from './components/chat/ChatInterface';
import { Message, MessageContent } from './components/chat/ChatInterface';
import { IJobResponse } from './types/IJob';
import { getMessage } from './api/chat';
import { useJob } from './context/JobContext';

function App() {
    const [isLoadingMessage, setIsLoadingMessage] = useState(false);
    const [messages, setMessages] = useState<Message[]>([
        {
            id: '1',
            content: 'Hello, I am your job search assistant. How can I help you today?',
            isUser: false,
        },
    ]);
    const { jobSelected } = useJob();

    const handleSendMessage = async (message: string) => {
        if (!message.trim()) return;

        const userMessage: Message = {
            id: Date.now().toString(),
            content: message,
            isUser: true,
        };

        setMessages((prev) => [...prev, userMessage]);

        setIsLoadingMessage(true);
        try {
            const response: IJobResponse = await getMessage({
                message,
                jobId: jobSelected?.jobId || null,
            });
            const responseContent: MessageContent = response.jobs || response.message;

            const botResponse: Message = {
                id: (Date.now() + 1).toString(),
                content: responseContent,
                isUser: false,
            };

            setMessages((prev) => [...prev, botResponse]);
        } catch (error) {
            console.error('Error al obtener la respuesta:', error);
        } finally {
            setIsLoadingMessage(false);
        }
    };

    return (
        <div className="flex flex-col h-screen bg-background">
            <ChatInterface
                messages={messages}
                onSendMessage={handleSendMessage}
                isLoadingMessage={isLoadingMessage}
            />
        </div>
    );
}

export default App;
