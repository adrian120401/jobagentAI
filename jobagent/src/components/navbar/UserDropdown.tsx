import { Button } from '../ui/button';

interface UserDropdownProps {
    setIsMenuOpen: (isMenuOpen: boolean) => void;
    logout: () => void;
    setIsUserMenuOpen: (isUserMenuOpen: boolean) => void;
}

const UserDropdown = ({ setIsMenuOpen, logout, setIsUserMenuOpen }: UserDropdownProps) => {
    return (
        <div className="absolute right-0 top-full mt-2 bg-card border border-border rounded-md shadow-md py-2 w-48 z-10">
            <Button
                variant="ghost"
                className="w-full justify-start px-4 py-2 text-sm h-auto"
                onClick={() => {
                    setIsMenuOpen(false);
                    setIsUserMenuOpen(true);
                }}
            >
                Profile
            </Button>
            <Button
                variant="ghost"
                className="w-full justify-start px-4 py-2 text-sm h-auto"
                onClick={() => {
                    setIsMenuOpen(false);
                    setIsUserMenuOpen(true);
                }}
            >
                Interviews history
            </Button>
            <Button
                variant="ghost"
                className="w-full justify-start px-4 py-2 text-destructive text-sm h-auto"
                onClick={() => {
                    setIsMenuOpen(false);
                    logout();
                }}
            >
                Logout
            </Button>
        </div>
    );
};

export default UserDropdown;
