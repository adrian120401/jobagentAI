import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import { ThemeProvider } from './components/ThemeProvider';
import { JobProvider } from './context/JobContext.tsx';

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ThemeProvider defaultTheme="dark">
            <JobProvider>
                <App />
            </JobProvider>
        </ThemeProvider>
    </StrictMode>
);
