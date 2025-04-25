import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { FileText, Upload, Check, X } from 'lucide-react';
import { uploadDocx } from '@/api/user';
import { useState } from 'react';
import { IUser } from '@/types/IUser';

interface DocxUploadProps {
    user: IUser;
    setUser: (user: IUser) => void;
}

const DocxUpload = ({ user, setUser }: DocxUploadProps) => {
    const [docxFile, setDocxFile] = useState<File | null>(null);
    const [isDragging, setIsDragging] = useState(false);
    const [uploadStatus, setUploadStatus] = useState<'idle' | 'uploading' | 'success' | 'error'>(
        'idle'
    );

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            const file = event.target.files[0];
            if (file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                setDocxFile(file);
                setUploadStatus('idle');
            } else {
                alert('Please select a DOCX file.');
            }
        }
    };

    const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(false);
        if (event.dataTransfer.files && event.dataTransfer.files[0]) {
            const file = event.dataTransfer.files[0];
            if (file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                setDocxFile(file);
                setUploadStatus('idle');
            } else {
                alert('Please select a DOCX file.');
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
        if (!docxFile) return;
        setUploadStatus('uploading');
        try {
            const userFile = await uploadDocx(docxFile);
            setUploadStatus('success');
            setDocxFile(null);
            const currentUser = user!;
            setUser({ ...currentUser, docx_path: userFile.url });
        } catch (error) {
            setUploadStatus('error');
            console.error('Error:', error);
        }
    };

    return (
        <div className="space-y-4">
            <Label htmlFor="docx-upload" className="text-sm font-medium">
                Update Resume (DOCX)
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
                {user.docx_path && !docxFile && (
                    <a
                        href={user.docx_path}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-2 text-sm text-primary hover:underline mb-4 p-2 rounded-md bg-primary/10 max-w-full"
                    >
                        <FileText className="h-8 w-8 flex-shrink-0" />
                        <span className="truncate max-w-[200px]">
                            {user.docx_path.split('/').pop() || 'View Current Document'}
                        </span>
                    </a>
                )}

                {docxFile && (
                    <div className="flex items-center gap-2 text-sm text-foreground mb-4 p-2 rounded-md bg-muted max-w-full">
                        <FileText className="h-8 w-8 flex-shrink-0" />
                        <span className="truncate">{docxFile.name}</span>
                    </div>
                )}

                {!user.docx_path && !docxFile && (
                    <FileText className="mx-auto h-10 w-10 text-muted-foreground mb-2" />
                )}

                <div className="flex text-sm text-muted-foreground">
                    <Label
                        htmlFor="docx-upload-input"
                        className="relative cursor-pointer rounded-md font-medium text-primary focus-within:outline-none focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2 hover:text-primary/80"
                    >
                        <span>
                            {docxFile
                                ? 'Change file'
                                : user.docx_path
                                ? 'Replace Document'
                                : 'Select a file'}
                        </span>
                        <Input
                            id="docx-upload-input"
                            name="docx-upload"
                            type="file"
                            className="sr-only"
                            accept=".docx"
                            onChange={handleFileChange}
                        />
                    </Label>
                    <p className="pl-1">or drag it here</p>
                </div>

                {!user.docx_path && !docxFile && (
                    <p className="text-xs text-muted-foreground mt-1">
                        Upload your document in DOCX format
                    </p>
                )}
            </div>
            {docxFile && (
                <Button
                    onClick={handleUpload}
                    className="w-full"
                    size="sm"
                    disabled={uploadStatus === 'uploading' || uploadStatus === 'success'}
                >
                    {uploadStatus === 'idle' && (
                        <>
                            <Upload className="mr-2 h-4 w-4" /> Upload New Document
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

export default DocxUpload;
