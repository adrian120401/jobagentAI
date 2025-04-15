import { ChangeEvent } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Send, X } from 'lucide-react';
import { useJob } from '@/context/JobContext';
interface InputFormProps {
    inputValue: string;
    setInputValue: (value: string) => void;
    handleSubmit: (e: React.FormEvent) => void;
}

const InputForm = ({ inputValue, setInputValue, handleSubmit }: InputFormProps) => {
    const { jobSelected, setJobSelected } = useJob();
    return (
        <div className="border border-border rounded-lg p-4 mb-4">
            <form onSubmit={handleSubmit} className="flex items-center gap-2">
                {jobSelected && (
                    <div className="flex items-center gap-2 border border-border rounded-lg p-2">
                        <span className="text-xs text-muted-foreground max-w-[100px] truncate">
                            {jobSelected.job.title}
                        </span>
                        <Button
                            className="h-4 w-4"
                            variant="ghost"
                            size="icon"
                            onClick={() => setJobSelected(null)}
                        >
                            <X className="h-4 w-4" />
                        </Button>
                    </div>
                )}
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
                    placeholder="Escribe un mensaje..."
                    className="rounded-full"
                />
                <Button
                    type="submit"
                    size="icon"
                    disabled={!inputValue.trim()}
                    className="rounded-full"
                >
                    <Send className="h-5 w-5" />
                </Button>
            </form>
        </div>
    );
};

export default InputForm;
