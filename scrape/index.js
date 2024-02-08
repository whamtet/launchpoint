const puppeteer = require('puppeteer');
const fs = require('fs');

const imgsToData = images => images.map(img => [img.src, img.parentElement.parentElement.children[1].innerText]);

(async () => {
    const browser = await puppeteer.launch({headless: false});
    const page = await browser.newPage();
    page.goto('https://companieslogo.com/logos/a/1/');
    page.on('domcontentloaded', async () => {
        const data = await page.$$eval('img.img-logo-small', imgsToData);
        const url = page.url();
        console.log('url', url);
        if (url.includes('/logos/')) {
            const filename = url.split('/logos/')[1].replaceAll('/', '-');

            fs.writeFileSync(`companies/${filename}`, JSON.stringify(data));
            console.log('wrote', filename);

        }

    });


})();
