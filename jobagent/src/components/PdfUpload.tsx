import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { FileText, Upload, Check, X } from 'lucide-react';
import { uploadCV } from '@/api/user';
import { useState } from 'react';
import { IUser } from '@/types/IUser';

interface PdfUploadProps {
    user: IUser;
    setUser: (user: IUser) => void;
}

const PdfUpload = ({ user, setUser }: PdfUploadProps) => {
    const [cvFile, setCvFile] = useState<File | null>(null);
    const [isDragging, setIsDragging] = useState(false);
    const [uploadStatus, setUploadStatus] = useState<'idle' | 'uploading' | 'success' | 'error'>(
        'idle'
    );

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            const file = event.target.files[0];
            if (file.type === 'application/pdf') {
                setCvFile(file);
                setUploadStatus('idle');
            } else {
                alert('Please select a PDF file.');
            }
        }
    };

    const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(false);
        if (event.dataTransfer.files && event.dataTransfer.files[0]) {
            const file = event.dataTransfer.files[0];
            if (file.type === 'application/pdf') {
                setCvFile(file);
                setUploadStatus('idle');
            } else {
                alert('Please select a PDF file.');
            }
        }
    };

    const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
    };

    const handleDragEnter = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(true);
    };

    const handleDragLeave = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(false);
    };

    const handleUpload = async () => {
        if (!cvFile) return;
        setUploadStatus('uploading');
        try {
            const userFile = await uploadCV(cvFile);
            setUploadStatus('success');
            setCvFile(null);
            const currentUser = user!;
            setUser({ ...currentUser, cv_path: userFile.url });
        } catch (error) {
            setUploadStatus('error');
            console.error('Error:', error);
        }
    };

    return (
        <div className="space-y-4">
            <Label htmlFor="cv-upload" className="text-sm font-medium">
                Update Resume (PDF)
            </Label>
            <div
                className={`flex flex-col items-center justify-center rounded-md border-2 border-dashed p-6 text-center ${
                    isDragging ? 'border-primary' : 'border-border'
                }`}
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragEnter={handleDragEnter}
                onDragLeave={handleDragLeave}
            >
                {user.cv_path && !cvFile && (
                    <a
                        href={user.cv_path}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-2 text-sm text-primary hover:underline mb-4 p-2 rounded-md bg-primary/10 max-w-full"
                    >
                        <FileText className="h-8 w-8 flex-shrink-0" />
                        <span className="truncate max-w-[200px]">
                            {user.cv_path.split('/').pop() || 'View Current CV'}
                        </span>
                    </a>
                )}

                {cvFile && (
                    <div className="flex items-center gap-2 text-sm text-foreground mb-4 p-2 rounded-md bg-muted max-w-full">
                        <FileText className="h-8 w-8 flex-shrink-0" />
                        <span className="truncate">{cvFile.name}</span>
                    </div>
                )}

                {!user.cv_path && !cvFile && (
                    <FileText className="mx-auto h-10 w-10 text-muted-foreground mb-2" />
                )}

                <div className="flex text-sm text-muted-foreground">
                    <Label
                        htmlFor="cv-upload-input"
                        className="relative cursor-pointer rounded-md font-medium text-primary focus-within:outline-none focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2 hover:text-primary/80"
                    >
                        <span>
                            {cvFile
                                ? 'Change file'
                                : user.cv_path
                                ? 'Replace CV'
                                : 'Select a file'}
                        </span>
                        <Input
                            id="cv-upload-input"
                            name="cv-upload"
                            type="file"
                            className="sr-only"
                            accept=".pdf"
                            onChange={handleFileChange}
                        />
                    </Label>
                    <p className="pl-1">or drag it here</p>
                </div>

                {!user.cv_path && !cvFile && (
                    <p className="text-xs text-muted-foreground mt-1">
                        Upload your resume in PDF format
                    </p>
                )}
            </div>
            {cvFile && (
                <Button
                    onClick={handleUpload}
                    className="w-full"
                    size="sm"
                    disabled={uploadStatus === 'uploading' || uploadStatus === 'success'}
                >
                    {uploadStatus === 'idle' && (
                        <>
                            <Upload className="mr-2 h-4 w-4" /> Upload New CV
                        </>
                    )}
                    {uploadStatus === 'uploading' && 'Uploading...'}
                    {uploadStatus === 'success' && (
                        <>
                            <Check className="mr-2 h-4 w-4" /> Uploaded
                        </>
                    )}
                    {uploadStatus === 'error' && (
                        <>
                            <X className="mr-2 h-4 w-4" /> Error, try again
                        </>
                    )}
                </Button>
            )}
        </div>
    );
};

export default PdfUpload;
