import csv
import uuid
import random
import datetime
def generate_todo_list(user_id, num_todos=200):
    description_choices = [
        # Eckhart Tolle quotes
        "The primary cause of unhappiness is never the situation but your thoughts about it.",
        "Life is the dancer and you are the dance.",
        # Napoleon Hill quotes
        "Whatever the mind can conceive and believe, it can achieve.",
        "You are the master of your destiny.",
        # Ralph Waldo Emerson quotes
        "To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.",
        "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
        # Marcus Aurelius quotes
        "You have power over your mind - not outside events. Realize this, and you will find strength.",
        "The best revenge is to be unlike him who performed the injustice.",
        # Bruce Lee quotes
        "Be like water making its way through cracks.",
        "Absorb what is useful, discard what is not, add what is uniquely your own.",
        # Deepak Chopra quotes
        "Every time you are tempted to react in the same old way, ask if you want to be a prisoner of the past or a pioneer of the future.",
        "In the midst of movement and chaos, keep stillness inside of you.",
        # Muhammad Ali quotes
        "He who is not courageous enough to take risks will accomplish nothing in life.",
        "Float like a butterfly, sting like a bee.",
        # Martin Luther King Jr quotes
        "Darkness cannot drive out darkness; only light can do that. Hate cannot drive out hate; only love can do that.",
        "The time is always right to do what is right.",
        # Henry David Thoreau quotes
        "I went to the woods because I wished to live deliberately, to front only the essential facts of life.",
        "Our life is frittered away by detail... simplify, simplify.",
        # Add more quotes here to reach 100...
        # Eckhart Tolle quotes
        "Awareness is the greatest agent for change.",
        "Life will give you whatever experience is most helpful for the evolution of your consciousness.",
        "The past has no power over the present moment.",
        "Sometimes letting things go is an act of far greater power than defending or hanging on.",
        "Realize deeply that the present moment is all you have."

        # Napoleon Hill quotes
        "Patience, persistence and perspiration make an unbeatable combination for success.",
        "Strength and growth come only through continuous effort and struggle.",
        "Every adversity, every failure, every heartache carries with it the seed of an equal or greater benefit.",
        "If you cannot do great things, do small things in a great way.",
        "It is literally true that you can succeed best and quickest by helping others to succeed."

        # Ralph Waldo Emerson quotes
        "Do not go where the path may lead, go instead where there is no path and leave a trail.",
        "For every minute you are angry you lose sixty seconds of happiness.",
        "Write it on your heart that every day is the best day in the year.",
        "The only way to have a friend is to be one.",
        "The purpose of life is not to be happy. It is to be useful, to be honorable, to be compassionate."

        # Marcus Aurelius (Roman) quotes
        "The best revenge is to be unlike him who performed the injustice.",
        "Very little is needed to make a happy life; it is all within yourself, in your way of thinking.",
        "When you arise in the morning, think of what a precious privilege it is to be alive.",
        "Accept the things to which fate binds you, and love the people with whom fate brings you together.",
        "Think of yourself as dead. You have lived your life. Now take what’s left and live it properly."

        # Bruce Lee quotes
        "I fear not the man who has practiced 10,000 kicks once, but I fear the man who has practiced one kick 10,000 times.",
        "Mistakes are always forgivable, if one has the courage to admit them.",
        "If you spend too much time thinking about a thing, you'll never get it done.",
        "Knowledge will give you power, but character respect.",
        "To hell with circumstances; I create opportunities."

        # Deepak Chopra quotes
        "Every great change is preceded by chaos.",
        "The less you open your heart to others, the more your heart suffers.",
        "In the midst of movement and chaos, keep stillness inside of you.",
        "Whatever relationships you have attracted in your life at this moment, are precisely the ones you need in your life at this moment.",
        "Silence is the great teacher, and to learn its lessons you must pay attention to it."

        # Muhammad Ali quotes
        "Service to others is the rent you pay for your room here on earth.",
        "Don’t count the days; make the days count.",
        "It's not bragging if you can back it up.",
        "I am the greatest, I said that even before I knew I was.",
        "Impossible is just a big word thrown around by small men."

        # Martin Luther King Jr. quotes
        "Faith is taking the first step even when you don't see the whole staircase.",
        "Injustice anywhere is a threat to justice everywhere.",
        "Our lives begin to end the day we become silent about things that matter.",
        "I have decided to stick with love. Hate is too great a burden to bear.",
        "Life's most persistent and urgent question is, 'What are you doing for others?'"

        # Henry David Thoreau quotes
        "Go confidently in the direction of your dreams! Live the life you've imagined.",
        "What you get by achieving your goals is not as important as what you become by achieving your goals.",
        "It's not what you look at that matters, it's what you see.",
        "Success usually comes to those who are too busy to be looking for it.",
        "Most men lead lives of quiet desperation and go to the grave with the song still in them."

        # Dave Chappelle quotes
        "The hardest thing to do is to be true to yourself, especially when everybody is watching.",
        "Sometimes the funniest thing to do is not say anything.",
        "I'm cool with failing so long as I know that there are people around me that love me unconditionally.",
        "You can become famous but you can't become unfamous. You can become infamous but not unfamous.",
        "My wife asked me once if I weren't a comedian what I would do. I couldn't answer the question. I never imagined doing anything else."

    ]

    todos = []
    for _ in range(num_todos):
        todo_id = str(uuid.uuid4())
        title = f"Todo {_+1}"
        description = random.choice(description_choices)
        completed = random.choice([True, False])
        todos.append([todo_id, title, description, completed, user_id])
    return todos


# add tiimestamp to filename
def write_to_csv(todos):
    # append timestamp to file name
    timestamp = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    with open(f'../resources/files/todos-{timestamp}', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['id', 'title', 'description', 'completed', 'userId'])
        writer.writerows(todos)

def main():
    user_id = input("Enter the user ID: ")
    todos = generate_todo_list(user_id)
    write_to_csv(todos)
    print(f"{len(todos)} todos have been written to todos.csv")

if __name__ == "__main__":
    main()
