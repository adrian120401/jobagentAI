import { useUser } from '@/context/UserContext';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
} from '@/components/ui/dialog';
import { Card, CardContent } from '@/components/ui/card';
import PdfUpload from '../PdfUpload';
import DocxUpload from '../DocxUpload';
interface UserMenuProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
}

const UserMenu = ({ isOpen, setIsOpen }: UserMenuProps) => {
    const { user, setUser } = useUser();

    if (!user) return null;

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-md p-0 border-none">
                <Card className="w-full shadow-none border-none">
                    <DialogHeader className="p-6 pb-4 space-y-1">
                        <DialogTitle className="text-xl font-semibold text-center">
                            My Profile
                        </DialogTitle>
                        <DialogDescription className="text-center">
                            {user.name}{' '}
                            <span className="text-muted-foreground">({user.email})</span>
                        </DialogDescription>
                    </DialogHeader>
                    <CardContent className="p-6 pt-0">
                        <div className="space-y-8">
                            <PdfUpload user={user} setUser={setUser} />
                            <DocxUpload user={user} setUser={setUser} />
                        </div>
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
};

export default UserMenu;
