import { AvatarFallback, AvatarImage } from "../ui/avatar";
import { ThemeToggle } from "../ThemeToggle";
import { Avatar } from "../ui/avatar";
import { Button } from "../ui/button";

interface NavBarProps {
    isMenuOpen: boolean;
    setIsMenuOpen: (isMenuOpen: boolean) => void;
}

export const Navbar = ({ isMenuOpen, setIsMenuOpen }: NavBarProps) => {
    return (
        <header className="border-b border-border py-3 px-4 flex items-center justify-between h-14 bg-card">
            <div className="text-lg font-medium">JobAgent</div>

            <div className="flex items-center gap-2">
                <ThemeToggle />

                <div className="relative">
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => setIsMenuOpen(!isMenuOpen)}
                        className="rounded-full"
                    >
                        <Avatar>
                            <AvatarImage src="" alt="Usuario" />
                            <AvatarFallback className="bg-primary text-primary-foreground">
                                U
                            </AvatarFallback>
                        </Avatar>
                    </Button>

                    {isMenuOpen && (
                        <div className="absolute right-0 top-full mt-2 bg-card border border-border rounded-md shadow-md py-2 w-48 z-10">
                            <Button
                                variant="ghost"
                                className="w-full justify-start px-4 py-2 text-sm h-auto"
                                onClick={() => setIsMenuOpen(false)}
                            >
                                Mi Perfil
                            </Button>
                            <Button
                                variant="ghost"
                                className="w-full justify-start px-4 py-2 text-sm h-auto"
                                onClick={() => setIsMenuOpen(false)}
                            >
                                Configuración
                            </Button>
                            <Button
                                variant="ghost"
                                className="w-full justify-start px-4 py-2 text-destructive text-sm h-auto"
                                onClick={() => setIsMenuOpen(false)}
                            >
                                Cerrar Sesión
                            </Button>
                        </div>
                    )}
                </div>
            </div>
        </header>
    );
};
