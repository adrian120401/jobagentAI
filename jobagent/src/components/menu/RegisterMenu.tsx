import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { EyeIcon, EyeOffIcon, LockIcon, MailIcon, UserIcon } from 'lucide-react';
import { Dialog, DialogContent } from '@/components/ui/dialog';
import { register } from '@/api/auth';

interface RegisterMenuProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    openLogin: () => void;
}

export function RegisterMenu({ isOpen, setIsOpen, openLogin }: RegisterMenuProps) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const openLoginMenu = () => {
        setIsOpen(false);
        openLogin();
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email || !password) return;

        setIsLoading(true);
        try {
            await register(email, password, name);
            setIsOpen(false);
            openLogin();
        } catch (error) {
            console.error('Error in register:', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-md p-0 border-none bg-transparent shadow-none">
                <Card className="w-full shadow-lg border-border/40">
                    <CardHeader className="space-y-2 relative">
                        <CardTitle className="text-2xl font-bold text-center">Register</CardTitle>
                        <CardDescription className="text-center">
                            Enter your credentials to create an account
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="mt-4">
                        <form onSubmit={handleSubmit} className="space-y-8">
                            <div className="space-y-2">
                                <Label htmlFor="name">Name</Label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-muted-foreground">
                                        <UserIcon className="h-4 w-4" />
                                    </div>
                                    <Input
                                        id="name"
                                        type="text"
                                        placeholder="Your name"
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                        className="pl-10"
                                        required
                                    />
                                </div>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="email">Email</Label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-muted-foreground">
                                        <MailIcon className="h-4 w-4" />
                                    </div>
                                    <Input
                                        id="email"
                                        type="email"
                                        placeholder="your@email.com"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="pl-10"
                                        required
                                    />
                                </div>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="password">Password</Label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-muted-foreground">
                                        <LockIcon className="h-4 w-4" />
                                    </div>
                                    <Input
                                        id="password"
                                        type={showPassword ? 'text' : 'password'}
                                        placeholder="********"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        className="pl-10 pr-10"
                                        required
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowPassword(!showPassword)}
                                        className="absolute inset-y-0 right-0 flex items-center pr-3 text-muted-foreground hover:text-foreground"
                                    >
                                        {showPassword ? (
                                            <EyeOffIcon className="h-4 w-4" />
                                        ) : (
                                            <EyeIcon className="h-4 w-4" />
                                        )}
                                    </button>
                                </div>
                            </div>
                            <Button type="submit" className="w-full" disabled={isLoading}>
                                {isLoading ? 'Registering...' : 'Register'}
                            </Button>
                            <Button
                                type="button"
                                variant="ghost"
                                className="w-full"
                                onClick={openLoginMenu}
                            >
                                Already have an account? Login
                            </Button>
                        </form>
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
}
