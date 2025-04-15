import { IJob } from '@/types/IJob';
import React, { createContext, useContext, useState, ReactNode } from 'react';

interface JobContextType {
    jobSelected: IJob | null;
    setJobSelected: (job: IJob | null) => void;
}

const JobContext = createContext<JobContextType | undefined>(undefined);

interface JobProviderProps {
    children: ReactNode;
}

export const JobProvider: React.FC<JobProviderProps> = ({ children }) => {
    const [jobSelected, setJobSelected] = useState<IJob | null>(null);

    const value = {
        jobSelected,
        setJobSelected,
    };

    return <JobContext.Provider value={value}>{children}</JobContext.Provider>;
};

export const useJob = (): JobContextType => {
    const context = useContext(JobContext);

    if (context === undefined) {
        throw new Error('useJob must be used inside a JobProvider');
    }

    return context;
};
