---
created: 2025-08-31T13:03:35Z
last_updated: 2025-08-31T13:03:35Z
version: 1.0
author: Claude Code PM System
---

# Product Context

## Product Overview

**Product Name**: Days Matter Clone (BigTime)
**Type**: Mobile Application - Android Native
**Category**: Productivity / Life Management
**Inspiration**: Days Matter (倒数日) - Popular Chinese countdown app

## Target Users

### Primary Users
- **Life Organizers**: People who track important personal dates
- **Students**: Tracking exam dates, deadlines, holidays
- **Professionals**: Managing project deadlines, meetings
- **Couples**: Anniversary and special date tracking
- **Parents**: Tracking children's milestones

### User Demographics
- Age: 18-45 years
- Tech comfort: Basic to intermediate
- Languages: English and Chinese (planned)
- Geographic focus: Global, with emphasis on Asian markets

## Core Functionality

### Essential Features

#### 1. Event Management
- **Create Events**: Add title, date, description, category
- **Edit Events**: Modify existing event details
- **Delete Events**: Remove events with confirmation
- **Archive Events**: Hide completed/past events

#### 2. Date Calculations
- **Countdown**: Days until future events
- **Count-up**: Days since past events
- **Today Marker**: Special indicator for current day
- **Dynamic Updates**: Real-time day calculations

#### 3. Event Organization
- **Categories**: Life, Work, Anniversary (customizable)
- **Pinned Events**: Keep important events at top
- **Search**: Find events quickly
- **Sorting**: By date, name, or days remaining

#### 4. Visual Presentation
- **Days Matter Card Style**: Distinctive card UI with large day display
- **Color Coding**: Different colors for categories
- **Grid/List Views**: Toggle between display modes
- **Dark Theme**: Support for dark mode

## Feature Requirements

### Implemented Features (35%)
✅ Basic event creation and storage
✅ Day calculation (count-up/countdown)
✅ Category system structure
✅ Days Matter UI style cards
✅ Navigation drawer
✅ Archive functionality
✅ Basic widget support
✅ Grid/List view toggle

### Partially Implemented (15%)
🔄 Category filtering
🔄 Event editing
🔄 Event deletion
🔄 Pinned events display

### Not Implemented (50%)

#### High Priority
❌ Lunar calendar support
❌ Repeat events (daily/weekly/monthly/yearly)
❌ Reminders and notifications
❌ Data backup and restore
❌ Share as image
❌ Event search functionality

#### Medium Priority
❌ Password protection
❌ Historical events ("历史上的今天")
❌ Multiple notebooks
❌ Custom categories
❌ Event templates
❌ Batch operations

#### Low Priority
❌ Cloud sync (iCity integration)
❌ Date calculator tool
❌ Milestone tracking
❌ Weather integration
❌ Social sharing
❌ Statistics and analytics

## User Stories

### Must Have
1. As a user, I want to create events with dates so I can track important occasions
2. As a user, I want to see how many days until/since an event
3. As a user, I want to categorize events for better organization
4. As a user, I want to pin important events so they're always visible
5. As a user, I want to receive reminders before important dates

### Should Have
1. As a user, I want to backup my events so I don't lose them
2. As a user, I want to set repeating events for recurring occasions
3. As a user, I want to share event images on social media
4. As a user, I want to use lunar calendar for traditional holidays
5. As a user, I want to protect my events with a password

### Nice to Have
1. As a user, I want to sync events across devices
2. As a user, I want to see historical events for any date
3. As a user, I want to calculate date differences
4. As a user, I want to track milestones (100 days, 1000 days)
5. As a user, I want to see event statistics

## Use Cases

### Primary Use Cases

#### UC1: Track Anniversary
- User creates anniversary event
- App shows days together
- User receives reminder each year
- Can share achievement on social media

#### UC2: Exam Countdown
- Student adds exam date
- App shows days remaining
- Daily notification updates
- Stress level indicator based on days left

#### UC3: Baby Milestones
- Parent tracks baby's age in days
- Special milestones highlighted (100 days, etc.)
- Photo memories integration
- Growth tracking features

#### UC4: Project Deadlines
- Professional adds work deadlines
- Multiple project tracking
- Priority indicators
- Team sharing capabilities

## Product Differentiation

### Compared to Original Days Matter
- **Similar**: Core countdown functionality, UI design language
- **Different**: Open source, no ads, privacy-focused
- **Missing**: Cloud sync, social features, premium themes

### Competitive Advantages
- Free and open source
- No advertisements
- Privacy-first approach
- Customizable and extendable
- Modern Android architecture

## Success Metrics

### User Engagement
- Daily active users (DAU)
- Events created per user
- Retention rate (7-day, 30-day)
- Widget usage rate

### Feature Adoption
- Category usage percentage
- Reminder setup rate
- Archive feature usage
- View mode preferences

### Technical Metrics
- App crash rate < 1%
- Cold start time < 2 seconds
- Memory usage < 100MB
- Battery impact minimal

## Constraints

### Technical Constraints
- Android 7.0+ support required
- Offline-first functionality
- Limited device storage usage
- Widget update frequency limits

### Resource Constraints
- Single developer project
- No external API dependencies (currently)
- Limited testing devices
- No dedicated design resources

### Market Constraints
- Competition from established apps
- User expectation of free features
- Need for multiple language support
- Cultural date considerations

## Future Considerations

### Monetization Options
- Premium themes
- Advanced features (cloud sync)
- Remove ads (if added)
- Donation model

### Platform Expansion
- iOS version
- Web version
- Wear OS companion
- Desktop widget

### Feature Expansion
- AI-powered event suggestions
- Integration with calendar apps
- Voice input support
- AR date visualization

## Localization Requirements

### Current
- English interface
- Gregorian calendar only

### Planned
- Chinese (Simplified/Traditional)
- Lunar calendar support
- Local holiday integration
- Regional date formats

## Privacy & Security

### Data Handling
- Local storage only (currently)
- No personal data collection
- Optional cloud backup (future)
- Encrypted sensitive events (planned)

### Permissions Required
- Storage (for backup/restore)
- Notifications (for reminders)
- Internet (future cloud sync)
- Camera (future photo memories)