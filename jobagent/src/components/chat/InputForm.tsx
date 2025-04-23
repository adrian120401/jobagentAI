import { ChangeEvent } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Send } from 'lucide-react';
import { cn } from '@/lib/utils';
interface InputFormProps {
    inputValue: string;
    setInputValue: (value: string) => void;
    handleSubmit: (e: React.FormEvent) => void;
    disabled?: boolean;
    className?: string;
}

const InputForm = ({ inputValue, setInputValue, handleSubmit, disabled, className }: InputFormProps) => {
    return (
        <div className={cn("border border-border rounded-lg p-4 mb-4", className)}>
            <form onSubmit={handleSubmit} className="flex items-center gap-2">
                <Input
                    type="text"
                    value={inputValue}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setInputValue(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter' && !e.shiftKey) {
                            e.preventDefault();
                            handleSubmit(e as unknown as React.FormEvent);
                        }
                    }}
                    placeholder="Write a message..."
                    className="rounded-full"
                />
                <Button
                    type="submit"
                    size="icon"
                    disabled={!inputValue.trim() || disabled}
                    className="rounded-full"
                >
                    <Send className="h-5 w-5" />
                </Button>
            </form>
        </div>
    );
};

export default InputForm;
