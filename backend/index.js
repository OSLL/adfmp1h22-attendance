const express = require('express')
const fs = require('fs')
const Users = require('./database/users.json')
const Lessons = require('./database/lessons.json')
const Checks = require('./database/checks.json')
const Groups = require('./database/groups.json')
const { use } = require('express/lib/application')

const port = 3001

const app = express()

function logger(req, res, next) {
    console.log(`[${Date.now()}] ${req.method} ${req.url}`)
    next()
}

app.use(logger)
app.use(express.json())

const TEACHERS = ['ks@mail.ru']

app.post('/users/register', (req, res) => {
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length === 0){
        const newUser = {
            id: `${Users.length}`,
            email: data.email,
            password: data.password,
            firstname: data.firstname,
            lastname: data.lastname,
            secondname: data.secondname,
            group: "1",
            telnum: data.telnum,
            status: TEACHERS.includes(data.email) ? "Teacher" : "Student"
        }
        Users.push(newUser)
        fs.writeFile('./database/users.json', JSON.stringify(Users, null, 4), 'utf8', (err) => {
            if(err){
                console.log(err)
                console.log("Error writing to users.json")
                res.status(200)
                res.send(JSON.stringify({status: "Internal server error"}))
            } else {
                res.status(200)
                res.send(JSON.stringify({status: "true", data: newUser}))
            }
        })
    } else {
        res.send(JSON.stringify({status: "This email already registered"}))
    }
})

app.post('/users/modify', (req, res) => {
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length === 1){
        Users.forEach(element => {
            if(element.id == data.id){
                element.surname = data.lastname
                element.firstname = data.firstname
                element.secondname = data.secondname
                element.email = data.email
                element.telnum = data.telnum
                element.status = TEACHERS.includes(data.email) ? "Teacher" : "Student"
                fs.writeFile('./database/users.json', JSON.stringify(Users, null, 4), 'utf8', (err) => {
                    if(err){
                        console.log(err)
                        console.log("Error writing to users.json")
                        res.status(200)
                        res.send(JSON.stringify({status: "Internal server error"}))
                        return
                    } else {
                        res.status(200)
                        res.send(JSON.stringify({status: "true", data: element}))
                        return
                    }
                })
            }
        })
    } else {
        res.status(200)
        res.send(JSON.stringify({status: "Sorry, can't modify for this e-mail"}))
    }
})

app.post('/users/login', (req, res) => {
    data = req.body
    console.log(data)
    if(Users.filter(user => user.email === data.email).length !== 0){
        user = Users.filter(user => user.email === data.email)[0]
        if(user.password === data.password){
            res.status(200)
            res.send(JSON.stringify({status: "true", data: user}))
            return
        } else {
            res.status(200)
            res.send(JSON.stringify({status: "Incorrect password for this email"}))
            return
        }
    }
    res.status(200)
    res.send({status: "There is no user with such email"})
})

app.post('/lessons/add', (req, res) => {
    data = req.body
    newLesson = {
        id: `${Lessons.length}`,
        name: data.name,
        group: data.group,
        date: data.date,
        time: data.time,
        position: {
            x: `${Lessons.length}`,
            y: `${Lessons.length}`
        }
    }
    Lessons.push(newLesson)
    fs.writeFile('./database/lessons.json', JSON.stringify(Lessons, null, 4), 'utf8', (err) => {
        if(err){
            console.log(err)
            console.log("Error writing to lessons.json")
            res.status(200)
            res.send(JSON.stringify({status: "Internal server error"}))
        } else {
            res.status(200)
            res.send(JSON.stringify({status: "true", data: newLesson}))
        }
    })
})

app.get('/lessons/:group/:userId', (req, res) => {
    group = req.params.group
    userId = req.params.userId
    const lessons = Lessons.filter(lesson => lesson.group === group)
        .filter(lesson => lesson.date.slice(0,2) == new Date().toISOString().slice(8,10)) //check day
        .filter(lesson => lesson.date.slice(3,5) == new Date().toISOString().slice(5,7)) ////check month
        .filter(lesson => lesson.date.slice(6,10) == new Date().toISOString().slice(0,4)) ////check year
    const lessonsChecked = Checks.filter(check => check.userId === userId)
        .map(check => check.lessonId)
    const lessonsDif = lessons.filter(lesson => !lessonsChecked.includes(lesson.id))
    res.status(200)
    res.send(JSON.stringify({status: "true", data: lessonsDif}))
})

function checkCoords(data, lesson) {
    if(Math.abs(lesson.position.x - data.positionX) < 100 && Math.abs(lesson.position.y - data.positionY) < 100) {
        return true
    }
    return false
}

app.post('/lessons/check/:lessonId/:userId', (req, res) => {
    lessonId = req.params.lessonId
    userId = req.params.userId
    data = req.body
    console.log(data.positionX)
    console.log(data.positionY)
    lessons = Lessons.filter(lesson => lesson.id === lessonId)
    if(lessons.length > 0 &&
        Users.filter(user => user.id === userId).length > 0){
            if(checkCoords(data, lessons[0])){
                Checks.push({
                    id: `${Checks.length}`,
                    lessonId: lessonId,
                    userId: userId,
                    status: "Was on lesson"
                })
                fs.writeFile('./database/checks.json', JSON.stringify(Checks, null, 4), 'utf8', (err) => {
                    if(err){
                        console.log("Error writing to checks.json")
                        res.status(200)
                        res.send(JSON.stringify({status: "Internal server error"}))
                    } else {
                        res.status(200)
                        res.send(JSON.stringify({status: "true"}))
                    }
                })
            } else {
                res.status(200)
                res.send(JSON.stringify({status: "You are too far"}))
            }
    } else {
        res.status(200)
        res.send(JSON.stringify({status: "There is no such user or lesson"}))
    }
})

app.get('/stats/student/:userId', (req, res) => {
    userId = req.params.userId
    lessons = []
    checks = Checks.filter(check => check.userId === userId)
    checks.forEach(check => {
        lessons.push(Lessons.filter(lesson => lesson.id === check.lessonId)[0])
    })
    items = []
    console.log(checks)
    for(let i = 0; i < checks.length; i++){
        items.push({id: checks[i].id, lesson: lessons[i].name, date: lessons[i].date, attendance: checks[i].status})
    }
    res.status(200)
    res.send(JSON.stringify({items: items}))
})

app.get('/stats/teacher/lessons/:teacherId', (req, res) => {
    teacherId = req.params.teacherId
    if(TEACHERS.includes(Users.filter(user => user.id === teacherId)[0].email)){
        lessonsToShow = Lessons.filter(lesson => Checks.filter(check => check.lessonId === lesson.id).length > 0)    //было, значит хоть кто-то на него пришёл
        numberOfVisits = []
        lessonsToShow.forEach(lesson => numberOfVisits.push(`${Checks.filter(check => check.lessonId === lesson.id).length} out of ${Groups.filter(group => group.id === lesson.group)[0].studentsCount}`))
        lessons = []
        for(let i = 0; i < lessonsToShow.length; i++){
            lessons.push({id: lessonsToShow[i].id, name: lessonsToShow[i].name, date: lessonsToShow[i].date, numberOfVisits: numberOfVisits[i]})
        }
        res.status(200)
        res.send(JSON.stringify({lessons: lessons}))
    } else {
        res.status(403)  
    }
})

app.get('/stats/teacher/students/:teacherId', (req, res) => {
    teacherId = req.params.teacherId
    if(TEACHERS.includes(Users.filter(user => user.id === teacherId)[0].email)){
        students = Users.filter(user => !TEACHERS.includes(user.email))
        lessonsForEachStudent = []
        items = []
        students.forEach(student => {
            lessons = []
            Lessons.filter(lesson => lesson.group === student.group).forEach(lesson => (!lessons.includes(lesson.name)) ? lessons.push(lesson.name) : {})
            lessonsForEachStudent.push(lessons)
        })
        for(let i = 0; i < lessonsForEachStudent.length; i++){
            oneUserChecks = Checks.filter(check => check.userId === students[i].id)
            lessonForCheck = []
            for(let j = 0; j < oneUserChecks.length; j++){
                lessonForCheck.push(Lessons.filter(lesson => lesson.id === oneUserChecks[j].lessonId)[0])
            }
            console.log(lessonForCheck)
            for(let j = 0; j < lessonsForEachStudent[i].length; j++){
                items.push({
                    lesson: lessonsForEachStudent[i][j],
                    fullname: `${students[i].lastname} ${students[i].firstname} ${students[i].secondname}`,
                    numberOfVisits: `${lessonForCheck.filter(lesson => lesson.name === lessonsForEachStudent[i][j]).length} out of ${Lessons.filter(lesson => lesson.group === students[i].group && lesson.name === lessonsForEachStudent[i][j]).length}`
                })
            }
        }
        res.status(200)
        res.send(JSON.stringify({items: items}))
        return
    } else {
        res.status(403)
    }
})

app.listen(port, () => console.log('Server listening on port ' + port))