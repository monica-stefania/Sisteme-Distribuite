window.addEventListener("load", function () {
    updateTable();
});

function updateTable() {
    fetch("/agenda").then((response) => response.json()).then((data) => {
        const table = document.getElementById("persoane");
        // delete all rows except header
        table.getElementsByTagName("tbody")[0].innerHTML = table.rows[0].innerHTML;
        // add current data
        for(let i = 0; i < data.length; ++i){
            let row = table.insertRow(i + 1);
            row.insertCell(0).innerText = data[i].id;
            row.insertCell(1).innerText = data[i].lastName;
            row.insertCell(2).innerText = data[i].firstName;
            row.insertCell(3).innerText = data[i].telephoneNumber;
            row.insertCell(4).innerHTML = "<button onclick=\"deletePerson(" + data[i].id + ")\">Sterge</button>>";
        }
    });
}

async function deletePerson(index) {
    await fetch("/person/" + index, {method: "DELETE"});
    updateTable();
}

async function addPerson() {
    let person = {};
    person["id"] = parseInt(document.getElementById("id").value);
    person["lastName"] = document.getElementById("lastName").value;
    person["firstName"] = document.getElementById("firstName").value;
    person["telephoneNumber"] = document.getElementById("telephoneNumber").value;

    await fetch("/person", {method: "POST", headers: {"Content-Type" : "application/json"}, body: JSON.stringify(person)});
    updateTable();
}