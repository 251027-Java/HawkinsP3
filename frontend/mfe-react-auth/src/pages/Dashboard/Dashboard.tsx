import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { progressApi } from '../../api/client';
import './Dashboard.css';

interface Analytics {
    totalQuizzesTaken: number;
    totalQuestionsAttempted: number;
    overallAccuracy: number;
    weakAreas: Array<{ categoryName: string; accuracyPercentage: number }>;
    strongAreas: Array<{ categoryName: string; accuracyPercentage: number }>;
    recentAttempts: Array<{ quizTitle: string; score: number; totalQuestions: number; completedAt: string }>;
}

const Dashboard: React.FC = () => {
    const { user, isAuthenticated } = useAuth();
    const [analytics, setAnalytics] = useState<Analytics | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!isAuthenticated) {
            window.history.pushState(null, '', '/login');
            window.dispatchEvent(new PopStateEvent('popstate'));
            return;
        }

        loadAnalytics();
    }, [isAuthenticated]);

    const loadAnalytics = async () => {
        try {
            const data = await progressApi.getAnalytics();
            setAnalytics(data);
        } catch (err: any) {
            setError('Failed to load analytics');
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    if (!isAuthenticated) return null;

    if (isLoading) {
        return (
            <div className="dashboard-loading">
                <div className="spinner"></div>
                <p>Loading your dashboard...</p>
            </div>
        );
    }

    return (
        <div className="dashboard">
            <div className="dashboard-header">
                <div>
                    <h1>Welcome back, {user?.firstName}! 👋</h1>
                    <p>Here's your training progress overview</p>
                </div>
                <a href="/quizzes" className="start-quiz-btn">
                    Start a Quiz →
                </a>
            </div>

            {error && (
                <div className="dashboard-error">
                    {error}
                </div>
            )}

            {analytics && (
                <>
                    {/* Stats Cards */}
                    <div className="stats-grid">
                        <div className="stat-card">
                            <div className="stat-icon">📝</div>
                            <div className="stat-content">
                                <span className="stat-value">{analytics.totalQuizzesTaken}</span>
                                <span className="stat-label">Quizzes Taken</span>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon">❓</div>
                            <div className="stat-content">
                                <span className="stat-value">{analytics.totalQuestionsAttempted}</span>
                                <span className="stat-label">Questions Answered</span>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon">🎯</div>
                            <div className="stat-content">
                                <span className="stat-value">{analytics.overallAccuracy?.toFixed(1) || 0}%</span>
                                <span className="stat-label">Overall Accuracy</span>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon">⭐</div>
                            <div className="stat-content">
                                <span className="stat-value">{analytics.strongAreas?.length || 0}</span>
                                <span className="stat-label">Strong Areas</span>
                            </div>
                        </div>
                    </div>

                    {/* Areas Grid */}
                    <div className="areas-grid">
                        <div className="area-card">
                            <h3>💪 Strong Areas</h3>
                            {analytics.strongAreas?.length > 0 ? (
                                <ul className="area-list">
                                    {analytics.strongAreas.slice(0, 5).map((area, index) => (
                                        <li key={index}>
                                            <span className="area-name">{area.categoryName}</span>
                                            <span className="area-score good">{area.accuracyPercentage?.toFixed(0)}%</span>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="no-data">Complete some quizzes to see your strengths!</p>
                            )}
                        </div>

                        <div className="area-card">
                            <h3>📚 Areas to Improve</h3>
                            {analytics.weakAreas?.length > 0 ? (
                                <ul className="area-list">
                                    {analytics.weakAreas.slice(0, 5).map((area, index) => (
                                        <li key={index}>
                                            <span className="area-name">{area.categoryName}</span>
                                            <span className="area-score needs-work">{area.accuracyPercentage?.toFixed(0)}%</span>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="no-data">Great job! No weak areas detected.</p>
                            )}
                        </div>
                    </div>

                    {/* Recent Attempts */}
                    <div className="recent-section">
                        <h3>📊 Recent Activity</h3>
                        {analytics.recentAttempts?.length > 0 ? (
                            <div className="attempts-list">
                                {analytics.recentAttempts.slice(0, 5).map((attempt, index) => (
                                    <div key={index} className="attempt-card">
                                        <div className="attempt-info">
                                            <span className="attempt-title">{attempt.quizTitle}</span>
                                            <span className="attempt-date">
                                                {new Date(attempt.completedAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <div className="attempt-score">
                                            {attempt.score}/{attempt.totalQuestions}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="no-data">No quiz attempts yet. Start practicing!</p>
                        )}
                    </div>
                </>
            )}
        </div>
    );
};

export default Dashboard;
