import { useState } from 'react';
import ChatInterface from './components/chat/ChatInterface';
import { Message, MessageContent } from './components/chat/ChatInterface';
import { jobs } from './data/data';

function App() {
    const [isLoadingMessage, setIsLoadingMessage] = useState(false);
    const [messages, setMessages] = useState<Message[]>([
        {
            id: '1',
            content: 'Hola, soy tu asistente de búsqueda de empleo. ¿En qué puedo ayudarte hoy?',
            isUser: false,
        },
    ]);

    const handleSendMessage = (message: string) => {
        if (!message.trim()) return;

        const userMessage: Message = {
            id: Date.now().toString(),
            content: message,
            isUser: true,
        };

        setMessages((prev) => [...prev, userMessage]);

        setIsLoadingMessage(true);

        setTimeout(() => {
            const responseContent: MessageContent = jobs;

            const botResponse: Message = {
                id: (Date.now() + 1).toString(),
                content: responseContent,
                isUser: false,
            };

            setMessages((prev) => [...prev, botResponse]);
            setIsLoadingMessage(false);
        }, 1000);
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
