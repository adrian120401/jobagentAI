import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { FileText, ExternalLink } from 'lucide-react';

interface DocumentCardProps {
    fileUrl: string;
}

const getFileName = (url: string) => {
    try {
        return decodeURIComponent(url.split('/').pop() || url);
    } catch {
        return url;
    }
};

const DocumentCard = ({ fileUrl }: DocumentCardProps) => {
    const fileName = getFileName(fileUrl);
    const isPdf = fileUrl.toLowerCase().endsWith('.pdf');
    const isDocx = fileUrl.toLowerCase().endsWith('.docx');
    const fileType = isPdf ? 'PDF Document' : isDocx ? 'Word Document' : 'File';

    return (
        <Card className="flex flex-row items-center gap-4 p-4 border border-border/40 bg-card shadow-sm">
            <div className="flex items-center justify-center w-14 h-14 rounded-md bg-muted">
                <FileText className="w-8 h-8 text-primary" />
            </div>
            <div className="flex-1 min-w-0">
                <div className="font-semibold text-primary truncate max-w-xs">{fileName}</div>
                <div className="text-xs text-muted-foreground mt-1">{fileType}</div>
            </div>
            <Button
                asChild
                variant="default"
                className="rounded-md px-3 py-2 flex items-center gap-1"
            >
                <a href={fileUrl} target="_blank" rel="noopener noreferrer">
                    Open
                    <ExternalLink size={16} />
                </a>
            </Button>
        </Card>
    );
};

export default DocumentCard;
