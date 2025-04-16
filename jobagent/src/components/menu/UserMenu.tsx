import { useState } from 'react';
import { useUser } from '@/context/UserContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Upload, FileText, Check, X } from 'lucide-react';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
} from '@/components/ui/dialog';
import { Card, CardContent } from '@/components/ui/card';

interface UserMenuProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
}

const UserMenu = ({ isOpen, setIsOpen }: UserMenuProps) => {
    const { user } = useUser();
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
                alert('Por favor, selecciona un archivo PDF.');
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
                alert('Por favor, selecciona un archivo PDF.');
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
        console.log('Subiendo archivo:', cvFile.name);
        try {
            // Lógica para subir el archivo al backend
            await new Promise((resolve) => setTimeout(resolve, 2000)); // Simulación
            const success = Math.random() > 0.3;
            setUploadStatus(success ? 'success' : 'error');
            if (success) {
                setCvFile(null);
                // Opcional: Actualizar user.cv_path en el contexto o refetch user
            }
        } catch (error) {
            console.error('Error al subir CV:', error);
            setUploadStatus('error');
        }
    };

    if (!user) return null;

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-md p-0 border-none">
                <Card className="w-full shadow-none border-none">
                    <DialogHeader className="p-6 pb-4 space-y-1">
                        <DialogTitle className="text-xl font-semibold text-center">
                            Mi Perfil
                        </DialogTitle>
                        <DialogDescription className="text-center">
                            {user.name}{' '}
                            <span className="text-muted-foreground">({user.email})</span>
                        </DialogDescription>
                    </DialogHeader>
                    <CardContent className="p-6 pt-0">
                        <div className="space-y-4">
                            <Label htmlFor="cv-upload" className="text-sm font-medium">
                                Actualizar CV (PDF)
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
                                            {user.cv_path.split('/').pop() || 'Ver CV Actual'}
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
                                                ? 'Cambiar archivo'
                                                : user.cv_path
                                                ? 'Reemplazar CV'
                                                : 'Selecciona un archivo'}
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
                                    <p className="pl-1">o arrástralo aquí</p>
                                </div>

                                {!user.cv_path && !cvFile && (
                                    <p className="text-xs text-muted-foreground mt-1">
                                        Sube tu currículum en formato PDF
                                    </p>
                                )}
                            </div>
                            {cvFile && (
                                <Button
                                    onClick={handleUpload}
                                    className="w-full"
                                    size="sm"
                                    disabled={
                                        uploadStatus === 'uploading' || uploadStatus === 'success'
                                    }
                                >
                                    {uploadStatus === 'idle' && (
                                        <>
                                            <Upload className="mr-2 h-4 w-4" /> Subir Nuevo CV
                                        </>
                                    )}
                                    {uploadStatus === 'uploading' && 'Subiendo...'}
                                    {uploadStatus === 'success' && (
                                        <>
                                            <Check className="mr-2 h-4 w-4" /> Subido
                                        </>
                                    )}
                                    {uploadStatus === 'error' && (
                                        <>
                                            <X className="mr-2 h-4 w-4" /> Error, intentar de nuevo
                                        </>
                                    )}
                                </Button>
                            )}
                        </div>
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
};

export default UserMenu;
