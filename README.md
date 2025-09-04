





cat > README.md <<'EOF'
# Student Management System (microservices)

**Rooms in our school (simple words):**
- `services/api-gateway` – front gate that routes requests to the right room
- `services/service-discovery` – school map (who/where each room is)
- `services/config-server` – shared notice board (settings)
- `services/auth-service` – makes ID cards (JWT tokens)
- `services/login-service` – door to show ID and get a ticket
- `services/course-service` – keeps course/class info
- `services/enrollment-service` – keeps who is in which class
- `services/notification-service` – sends messages/bells

## How we work (tiny steps)
1. Make a small change on your computer.
2. Save it: `git add .` → `git commit -m "message"` → `git push`.
3. Open a Pull Request for review.
