import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import { ThemeProvider } from './components/ThemeProvider';
import { JobProvider } from './context/JobContext.tsx';
import { UserProvider } from './context/UserContext.tsx';
import { ErrorProvider } from './context/ErrorContext.tsx';

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ThemeProvider defaultTheme="dark">
            <ErrorProvider>
                <UserProvider>
                    <JobProvider>
                        <App />
                    </JobProvider>
                </UserProvider>
            </ErrorProvider>
        </ThemeProvider>
    </StrictMode>
);
