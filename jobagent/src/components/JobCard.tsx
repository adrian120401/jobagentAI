import { IJob } from '@/types/IJob';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ExternalLink, CheckCircle } from 'lucide-react';
import { useJob } from '@/context/JobContext';
interface JobCardProps {
    job: IJob;
}

export const JobCard = ({ job }: JobCardProps) => {
    const { setJobSelected } = useJob();
    const matchPercentage = Math.round(job.matchScore * 100);
    const isHighMatch = matchPercentage >= 80;

    const radius = 18;
    const circumference = 2 * Math.PI * radius;
    const strokeDashoffset = circumference - (matchPercentage / 100) * circumference;

    return (
        <Card className="overflow-hidden bg-card hover:shadow-md transition-all duration-300 border border-border/40 pt-6 pb-0">
            <div className="flex px-2">
                <div className="w-[90px] h-[90px] bg-white flex-shrink-0 flex items-center justify-center rounded-md">
                    <img
                        src={job.job.companyLogo}
                        alt={job.job.companyName}
                        className="max-w-[80%] max-h-[80%] object-contain"
                    />
                </div>

                <div className="flex-1 px-4">
                    <div className="mb-1.5">
                        <span className="text-primary font-semibold">{job.job.title}</span>
                    </div>

                    <div className="mb-2">
                        <span className="text-sm text-muted-foreground">{job.job.companyName}</span>
                    </div>

                    <p className="text-sm text-muted-foreground line-clamp-2">{job.reason}</p>
                </div>

                <div className="w-20 flex-shrink-0 flex items-center justify-center relative border-l border-border/40">
                    <div className="relative flex items-center justify-center">
                        <svg width="50" height="50" className="transform -rotate-90">
                            <circle
                                cx="25"
                                cy="25"
                                r={radius}
                                stroke="currentColor"
                                strokeWidth="3"
                                fill="transparent"
                                className="text-muted/30"
                            />
                            <circle
                                cx="25"
                                cy="25"
                                r={radius}
                                stroke="currentColor"
                                strokeWidth="3"
                                fill="transparent"
                                strokeDasharray={circumference}
                                strokeDashoffset={strokeDashoffset}
                                strokeLinecap="round"
                                className={isHighMatch ? 'text-green-500' : 'text-amber-500'}
                            />
                        </svg>

                        <div className="absolute inset-0 flex items-center justify-center">
                            <span
                                className={`text-sm font-bold ${
                                    isHighMatch ? 'text-green-500' : 'text-amber-500'
                                }`}
                            >
                                {matchPercentage}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="border-t border-border/40">
                <div className="flex justify-between">
                    <Button
                        variant="ghost"
                        className="w-1/2 rounded-none text-xs font-medium h-12 hover:bg-muted/50"
                        onClick={() => setJobSelected(job)}
                    >
                        <span className="flex items-center gap-1.5">
                            Seleccionar
                            <CheckCircle size={14} />
                        </span>
                    </Button>
                    <Button
                        variant="default"
                        className="w-1/2 rounded-none text-xs font-medium h-12 bg-primary/90 hover:bg-primary"
                        asChild
                    >
                        <a
                            href={job.job.jobUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="flex items-center justify-center gap-1.5"
                        >
                            Ver Oferta
                            <ExternalLink size={14} />
                        </a>
                    </Button>
                </div>
            </div>
        </Card>
    );
};
