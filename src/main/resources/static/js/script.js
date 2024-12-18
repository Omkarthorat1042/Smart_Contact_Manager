console.log("This is script file")

const toggleSidebar = () => {
	
    if($('.sidebar').is(":visible")){
        $(".sidebar").css("display", "none");   
        $(".content").css("margin-left","0%");


    }else{
        $(".sidebar").css("display", "block");   
        $(".content").css("margin-left","20%");


    }




};

const search = () => {
    let query = $("#search-input").val(); // Get the search input value
    console.log("Search Query:", query); // Log the query to check if the function is triggered

    if (query === '') {
        $(".search-result").hide(); // Hide results if query is empty
    } else {
        console.log("Making request to:", `http://localhost:8081/search/${query}`);
        let url = `http://localhost:8081/search/${query}`;
        
        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log("Search Results:", data); // Log the fetched data
                let text = `<div class='list-group'>`;
                data.forEach(contact => {
                    // Update the URL to match the contact detail page
                    text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`;


                });
                text += `</div>`;
                $(".search-result").html(text).show(); // Inject results and show the container
            })
            .catch(error => console.error("Error fetching data:", error));
    }
};
























