import { useError } from '@/context/ErrorContext';
import {
    Dialog,
    DialogContent,
    DialogTitle,
    DialogDescription,
    DialogFooter,
    DialogHeader,
} from '../ui/dialog';
import { Button } from '../ui/button';

const ErrorDialog = () => {
    const { error, setError } = useError();

    const handleClose = () => {
        setError(false);
    };

    return (
        <Dialog open={error} onOpenChange={handleClose}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Error</DialogTitle>
                </DialogHeader>
                <DialogDescription>
                    <p>Error</p>
                </DialogDescription>
                <DialogFooter>
                    <Button onClick={handleClose}>Close</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
};

export default ErrorDialog;
