const fs = require('fs');
const path = require('path');
const docsDir = './docs/content/developer/'

const generateFileIndexMap = () => {
	const indexFilePath = './docs/dhis2_android_sdk_developer_guide_INDEX.yml'
    const indexContent = fs.readFileSync(indexFilePath, 'utf8');
    const lines = indexContent.split('\n');
    const filePositionMap = new Map();

    let position = 1;
    lines.forEach((line) => {
        const match = line.match(/"docs/content/developer/(.+\.md)/);
        if (match) {
            const filePath = match[1];
            filePositionMap.set(filePath, position);
            position++;
        }
    });
    return filePositionMap;
};

const generateLinkMap = () => {
	const linkMap = new Map();
	const files = fs.readdirSync(docsDir);
	const headerRegex = /^(#+) .+? { *#(.+?) *}/gm;
	files.forEach((file) => {
		let filePath = path.join(docsDir, file);
        if (!fs.statSync(filePath).isDirectory() && file.endsWith('.md')) {
            const content = fs.readFileSync(filePath, 'utf-8');
			const findArray = [...content.matchAll(headerRegex)];
			findArray.forEach((match) => {
				if (match[1].length === 1) {
					linkMap.set(match[2], file)
				} else {
					linkMap.set(match[2], file + '#' + match[2])
				}
			})
        }
	});
	return linkMap
};

const addSidebarPositionToFiles = (content, position) => {
	if (position) {
		if (!content.startsWith('---\nsidebar_position:')) {
			return `---\nsidebar_position: ${position}\n\n---\n\n` + content;
		}
	}
	return content;
};

const admonitionMap = new Map([
    ['Note', 'note'],
    ['Tip', 'tip'],
    ['Important', 'info'],
    ['Caution', 'warning'],
    ['Warning', 'danger'],
]);

const convertAdmonitions = (content, file) => {
	if (file.endsWith('.md')) {
		return content.replace(
			/>\s*\*\*(\w+)\*\*\s*\n>\n((?:>.*\n)+)/g,
			(match, keyword, p1) => {
				const type = admonitionMap.get(keyword);
				if (type) {
					return `:::${type}\n\n${p1.replaceAll(/>\s*/g, '')}:::\n`
				} else {
					return `:::note[${keyword}]\n\n${p1.replaceAll(/>\s*/g, '')}:::\n`
				}
			}
		);
	}
	return content;
};

const stripRefs = (content, file) => {
	if (file.endsWith('.md')) {
		return content.replace(
			/\{ *(.*) \}/g,
			(match, p1) => {
				return `{${p1}}`
			}
		);
	}
	return content
};

const processSymbol = (content, file) => {
	if (file.endsWith('.md')) {
		return content
		.replace(/\{width.*?\}/g, '')
		.replace(/<=/g, '')
		.replace(/</g, '');
	}
	return content
};

const processLinks = (content, file, linkMap) => {
	if (file.endsWith('.md')) {
		return content.replace(
			/\(#(.*?)\)/gm,
			(match, p1) => {
				return `(${linkMap.get(p1)})`
			}
		);
	}
	return content
}

const mainFileLoop = () => {
	const filePositionMap = generateFileIndexMap();
	const linkMap = generateLinkMap();
	const files = fs.readdirSync(docsDir);
	files.forEach((file) => {
		const filePath = path.join(docsDir, file);
        if (fs.statSync(filePath).isDirectory()) {
            // do nothing
        } else {
			// read file content
			let content = fs.readFileSync(filePath, 'utf8');
			// add index header
			const position = filePositionMap.get(file);
			content = addSidebarPositionToFiles(content);
			// convert emphasis contexts
			content = convertAdmonitions(content, file);
			// strip ref links
			content = stripRefs(content, file);
			// clean unprocessable symbols
			content = processSymbol(content, file);
			// process cross reference links
			content = processLinks(content, file, linkMap);
			// save processed content
			fs.writeFileSync(filePath, content, 'utf8');
		}
	})
}

mainFileLoop();