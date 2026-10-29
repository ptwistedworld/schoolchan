package com.schoolchan.staff;

/**
 * The rules text that ships with the board.
 *
 * <p>Staff can edit the rules from the settings page, which creates a new
 * version. This constant is the shipped default, and staff can restore it if
 * an edit went wrong.
 */
public final class DefaultRules {

    private DefaultRules() {
    }

    public static final String BODY = """
            # SchoolChan Rules
            A short, friendly list. If something here is unclear, ask a moderator.

            ## 1. Be decent to each other
            Argue with ideas, not people. No bullying, no shaming, no targeting
            anyone for who they are. This applies to students and to staff.

            ## 2. Keep other people's details off the board
            Do not post someone's real name, address, phone number, schedule, or
            anything that could be used to find or follow them. This includes
            screenshots and photos of other people without their clear permission.

            ## 3. Keep it school-appropriate
            No sexual content, no graphic violence, no drugs, no gambling, and
            nothing illegal. Tag anything spoilery as a spoiler so people can
            choose whether to open it.

            ## 4. No spam or scams
            No advertising, no referral links, no fake prizes, and no links to
            phishing or malware. Repeated reposting of the same thing counts as spam.

            ## 5. One account does the talking
            Do not use the board to argue with yourself, and do not organise a
            pile-on. If a thread turns into harassment, report it and step back.

            ## 6. Follow campus access hours
            The board is open during the hours staff set. Outside those hours you
            can read, but you cannot post. A staff override may open it early for
            a school event.

            ## 7. Report what should not be here
            Every post has a report button. Use it. Reports are read by staff.
            You do not need to say who you are to report, and you will not be
            punished for making one in good faith.

            ## How moderation works
            Everything you write is published straight away. Nothing is removed
            automatically. A filter, another student, or a moderator may point a
            post out to the staff team, and then a person reads it and decides
            whether it stays. If a post is taken down, it stays readable to you
            under My posts, and you can ask a moderator to look again.

            ## Escalation - when a post is a safety concern
            If a post reads like a genuine safety risk to the poster or to the
            school, it is hidden straight away and put in a restricted staff queue.
            A staff member may then forward the post, and the poster's network
            address, to the school administration or guidance office so a human
            can act. This is only for genuine safety risks - it is never used for
            ordinary disagreements. Nothing is ever sent automatically; a person
            always decides.

            ## Moderator conduct
            Moderators and admins are also anonymous to students, and they are open
            to the same criticism as anyone else on the board. Moderators may not
            use the network address or session information they can see for
            anything except a real abuse investigation or escalation, may not
            retaliate against anyone, and every time they open that information it
            is written in an audit log that admins can read.

            ## What we store, and for how long
            A random cookie in your browser identifies your browser so votes and
            bookmarks work; only a hash of it is kept. The network address a post
            came from is encrypted, stored separately from the post, and is deleted
            automatically after 90 days unless it is attached to an open safety case.
            It is never shown on the board and never attached to a post. Student
            sign-in, where the school requires it, is only a gate: it is never
            linked to anything you post.

            ## Data Privacy Act (RA 10173)
            We keep only what we need, we say what we keep, we delete it on
            schedule, and students can ask what is held about them. This page
            describes the design; it is not a substitute for the school's own
            legal review.
            """;
}
