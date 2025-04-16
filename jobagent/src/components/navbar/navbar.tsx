import { AvatarFallback, AvatarImage } from '../ui/avatar';
import { ThemeToggle } from '../ThemeToggle';
import { Avatar } from '../ui/avatar';
import { Button } from '../ui/button';
import { useState } from 'react';
import { LoginMenu } from '../menu/LoginMenu';
import UserDropdown from './UserDropdown';
import { useUser } from '@/context/UserContext';
import UserMenu from '../menu/UserMenu';

export const Navbar = () => {
    const [isLoginMenuOpen, setIsLoginMenuOpen] = useState(false);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

    const { isAuthenticated, user, logout } = useUser();

    return (
        <header className="border-b border-border py-3 px-4 flex items-center justify-between h-14 bg-card">
            <div className="text-lg font-medium">JobAgent</div>

            <div className="flex items-center gap-2">
                <ThemeToggle />

                <div className="relative">
                    {isAuthenticated ? (
                        <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => setIsMenuOpen(!isMenuOpen)}
                            className="rounded-full"
                        >
                            <Avatar>
                                <AvatarImage src="" alt="Usuario" />
                                <AvatarFallback className="bg-primary text-primary-foreground">
                                    {user?.name?.charAt(0)}
                                </AvatarFallback>
                            </Avatar>
                        </Button>
                    ) : (
                        <Button
                            variant="outline"
                            className=""
                            onClick={() => setIsLoginMenuOpen(true)}
                        >
                            Iniciar sesión
                        </Button>
                    )}

                    {isMenuOpen && (
                        <UserDropdown
                            setIsMenuOpen={setIsMenuOpen}
                            logout={logout}
                            setIsUserMenuOpen={setIsUserMenuOpen}
                        />
                    )}
                    <UserMenu isOpen={isUserMenuOpen} setIsOpen={setIsUserMenuOpen} />
                    <LoginMenu isOpen={isLoginMenuOpen} setIsOpen={setIsLoginMenuOpen} />
                </div>
            </div>
        </header>
    );
};
